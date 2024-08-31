package com.ampznetwork.libmod.core.database.hibernate;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.EntityAccessor;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.messaging.MessagingService;
import com.ampznetwork.libmod.api.messaging.NotifyEvent;
import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import com.ampznetwork.libmod.core.database.messaging.PollingMessagingService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.comroid.api.Polyfill;
import org.comroid.api.func.exc.ThrowingFunction;
import org.comroid.api.func.ext.Wrap;
import org.comroid.api.func.util.Debug;
import org.comroid.api.func.util.GetOrCreate;
import org.comroid.api.func.util.Streams;
import org.comroid.api.map.Cache;
import org.comroid.api.tree.Container;
import org.comroid.api.tree.UncheckedCloseable;
import org.hibernate.Session;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.comroid.api.Polyfill.*;

@Slf4j
@Value
@EqualsAndHashCode(of = "manager")
public class HibernateEntityService extends Container.Base implements IEntityService {
    private static final PersistenceProvider SPI = new HibernatePersistenceProvider();

    static {
        /*
        // register messaging service types
        REGISTRY.addAll(List.of(
                new MessagingService.Type<MessagingService.PollingDatabase.Config, PollingMessagingService>("polling-db") {
                    @Override
                    public PollingMessagingService createService(BanMod mod, EntityService entities, MessagingService.PollingDatabase.Config config) {
                        var dbInfo = config.dbInfo();
                        if (dbInfo != null && dbInfo.type() != null)
                            entities = new HibernateEntityService(mod, BanModMessagingPersistenceUnit::new, dbInfo);
                        if (entities instanceof HibernateEntityService hibernate)
                            return new PollingMessagingService(hibernate, config.interval());
                        return null;
                    }
                },
                new MessagingService.Type<MessagingService.RabbitMQ.Config, RabbitMessagingService>("rabbit-mq") {
                    @Override
                    public RabbitMessagingService createService(BanMod mod, EntityService entities, MessagingService.RabbitMQ.Config config) {
                        return new RabbitMessagingService(config.uri(), entities);
                    }
                }));
         */
    }

    public static Unit buildPersistenceUnit(
            DatabaseInfo info,
            Function<HikariDataSource, PersistenceUnitInfo> unitProvider,
            @MagicConstant(stringValues = { "update", "validate" }) String hbm2ddl
    ) {
        var config = Map.of("hibernate.connection.driver_class",
                info.type().getDriverClass().getCanonicalName(),
                "hibernate.connection.url",
                info.url(),
                "hibernate.connection.username",
                info.user(),
                "hibernate.connection.password",
                info.pass(),
                "hibernate.dialect",
                info.type().getDialectClass().getCanonicalName(),
                "hibernate.show_sql",
                String.valueOf("true".equals(System.getenv("TRACE"))),
                "hibernate.hbm2ddl.auto",
                hbm2ddl);
        var dataSource = new HikariDataSource() {{
            setDriverClassName(info.type().getDriverClass().getCanonicalName());
            setJdbcUrl(info.url());
            setUsername(info.user());
            setPassword(info.pass());
        }};
        var unit    = unitProvider.apply(dataSource);
        var factory = SPI.createContainerEntityManagerFactory(unit, config);
        var manager = factory.createEntityManager();
        return new Unit(dataSource, manager);
    }

    LibMod                   lib;
    EntityManager            manager;
    ScheduledExecutorService scheduler;
    @Nullable MessagingService messagingService;
    Map<String, EntityContainer<?, ?>> accessors = new ConcurrentHashMap<>();

    @ApiStatus.Experimental
    public HibernateEntityService(LibMod lib) {
        this(lib, dataSource -> new PersistenceUnitBase(LibMod.class, dataSource, lib.getRegisteredSubMods().stream()
                .flatMap(it -> it.getEntityTypes().stream())
                .toArray(Class[]::new)));
    }

    public HibernateEntityService(LibMod lib, SubMod mod) {
        this(lib, dataSource -> new PersistenceUnitBase(mod.getModuleType(), dataSource, mod.getEntityTypes().toArray(new Class[0])));
    }

    public HibernateEntityService(LibMod mod, Function<HikariDataSource, PersistenceUnitInfo> persistenceUnitProvider) {
        this(mod, persistenceUnitProvider, mod.getDatabaseInfo());
    }

    public HibernateEntityService(LibMod mod, Function<HikariDataSource, PersistenceUnitInfo> persistenceUnitProvider, DatabaseInfo dbInfo) {
        // boot up hibernate
        this.lib = mod;
        var unit = buildPersistenceUnit(dbInfo, persistenceUnitProvider, "update");
        this.manager = unit.manager;

        // boot up messaging service
        this.scheduler        = Executors.newScheduledThreadPool(2);
        this.messagingService = mod.getMessagingServiceType()
                .map(ThrowingFunction.fallback(type -> type.createService(mod, this, uncheckedCast(mod.getMessagingServiceConfig())), Wrap.empty()))
                .orElse(null);
        log.info("Using MessagingService " + messagingService);
        addChildren(unit, scheduler, messagingService);

        // cleanup task
        scheduler.scheduleAtFixedRate(() -> {
            if (messagingService instanceof PollingMessagingService polling) polling.cleanup();
            EntityType.REGISTRY.values().stream().map(EntityType::getCache).forEach(Cache::clear);
        }, 10, 10, TimeUnit.MINUTES);
    }

    @Override
    public <T extends DbObject, B extends DbObject.Builder<T, B>> EntityAccessor<T, B> getAccessor(EntityType<T, ? super B> type) {
        return uncheckedCast(accessors.computeIfAbsent(type.getDtype(), k -> new EntityContainer<>(uncheckedCast(type))));
    }

    @Override
    public <T extends DbObject> T save(T object) {
        var persistent = wrapTransaction(() -> {
            if (manager.contains(object)) try {
                manager.persist(object);
                // now a persistent object!
                return object;
            } catch (Throwable t) {
                Debug.log(log, "persist() failed for " + object, t);
            }
            // try merging as a fallback action
            return manager.merge(object);
        });
        if (!(object instanceof NotifyEvent)) Polyfill.<Cache<UUID, DbObject>>uncheckedCast(EntityType.REGISTRY.get(object.getDtype()).getCache())
                .push(persistent);
        if (messagingService != null)
            messagingService.push().complete(bld -> bld
                    .relatedId(object.getId())
                    .relatedType(Polyfill.uncheckedCast(object.getDtype())));
        return persistent;
    }

    @Override
    public void refresh(EntityType<?, ?> type, UUID... ids) {
        for (UUID id : ids) {
            DbObject dbObject = type.getCache().get(id);
            if (dbObject != null)
                manager.refresh(dbObject);
        }
    }

    @Override
    public void uncache(UUID id, @Nullable DbObject obj) {
        if (obj != null)
            obj.getDtype().getCache().remove(id);
    }

    @Override
    public int delete(@SuppressWarnings("rawtypes") DbObject... objects) {
        var transaction = manager.getTransaction();
        var c           = 0;
        synchronized (transaction) {
            try {
                transaction.begin();
                for (Object each : objects) {
                    each = manager.merge(each);
                    manager.remove(each);
                    c += 1;
                }
                manager.flush();
                transaction.commit();
            } catch (Throwable t) {
                transaction.rollback();
                LibMod.Resources.printExceptionWithIssueReportUrl(log, "Could not remove all entities", t);
            }
        }
        return c;
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T> T wrapQuery(Function<Query, T> executor, Query query) {return wrapQuery(Connection.TRANSACTION_READ_COMMITTED, executor, query);}

    @SuppressWarnings("UnusedReturnValue")
    public <T> T wrapQuery(@MagicConstant(valuesFromClass = Connection.class) int isolation, Function<Query, T> executor, Query query) {
        return wrapTransaction(isolation, new Supplier<>() {
            @Override
            public T get() {
                return executor.apply(query);
            }

            @Override
            public String toString() {
                return query.toString();
            }
        });
    }

    public <T> T wrapTransaction(Supplier<T> executor) {
        return wrapTransaction(Connection.TRANSACTION_READ_COMMITTED, executor);
    }

    public <T> T wrapTransaction(@MagicConstant(valuesFromClass = Connection.class) int isolation, Supplier<T> executor) {
        var transaction = manager.getTransaction();

        synchronized (transaction) {
            transaction.begin();

            try ( // need a session
                  var session = manager.unwrap(Session.class).getSessionFactory().openSession()
            ) {
                // isolate
                session.doWork(con -> con.setTransactionIsolation(isolation));

                // execute and commit
                var result = executor.get();
                transaction.commit();

                return result;
            } catch (Throwable t) {
                log.warn("Could not execute task " + executor, t);
                if (transaction.isActive()) transaction.rollback();
                throw t;
            }
        }
    }

    @Value
    private class EntityContainer<T extends DbObject, B extends DbObject.Builder<T, B>> implements EntityAccessor<T, B> {
        @lombok.experimental.Delegate EntityType<T, B> type;

        @Override
        public EntityManager getManager() {
            return manager;
        }

        @Override
        public IEntityService getService() {
            return HibernateEntityService.this;
        }

        @Override
        public Stream<T> all() {
            return manager.createQuery("select it from %s it".formatted(type.getDtype()), getEntityType()).getResultStream().peek(type.getCache()::push);
        }

        @Override
        public Stream<T> querySelect(Query query) {
            return HibernateEntityService.this.wrapQuery(q -> Polyfill.uncheckedCast(q.getResultStream()), query);
        }

        @Override
        public Optional<T> get(UUID id) {
            return type.getCache()
                    .wrap(id)
                    .stream()
                    .map(Polyfill::<T>uncheckedCast)
                    .collect(Streams.or(() -> manager.createQuery("select it from %s it where it.id = :id".formatted(type.getDtype()), getEntityType())
                            .setParameter("id", id)
                            .getResultStream()
                            .peek(type.getCache()::push)))
                    .findAny();
        }

        @Override
        public GetOrCreate<T, B> getOrCreate(UUID key) {
            return new GetOrCreate<>(key == null ? () -> null : () -> get(key).orElse(null),
                    () -> type.builder().id(key),
                    DbObject.Builder::build,
                    HibernateEntityService.this::save)
                    .addCompletionCallback(it -> {
                        // push to cache
                        type.getCache().push(it);

                        // push to messaging service
                        if (messagingService != null)
                            messagingService.push()
                                    .complete(notif -> notif.relatedId(it.getId())
                                            .relatedType(Polyfill.uncheckedCast(it.getDtype())));
                    });
        }

        @Override
        public void queryUpdate(Query query) {
            wrapQuery(Query::executeUpdate, query);
        }
    }

    public record Unit(HikariDataSource dataSource, EntityManager manager) implements UncheckedCloseable {
        @Override
        public void close() {
            dataSource.close();
            manager.close();
        }
    }
}
