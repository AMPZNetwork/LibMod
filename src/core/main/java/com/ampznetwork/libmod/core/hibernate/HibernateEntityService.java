package com.ampznetwork.libmod.core.hibernate;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Value;
import org.comroid.api.tree.Container;
import org.comroid.api.tree.UncheckedCloseable;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.intellij.lang.annotations.MagicConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Map;
import java.util.function.Function;

@Value
public class HibernateEntityService extends Container.Base implements EntityService {
    private static final PersistenceProvider SPI = new HibernatePersistenceProvider();
    private static final Logger log = LoggerFactory.getLogger(HibernateEntityService.class);
    LibMod libMod;
    EntityManager manager;

    public HibernateEntityService(LibMod libMod) {
        this.libMod = libMod;
        var unit = buildPersistenceUnit(libMod.getDatabaseInfo(), LibModPersistenceUnit::new, "update");
        this.manager = unit.manager;
        addChildren(unit);
    }

    public static Unit buildPersistenceUnit(
            DatabaseInfo info,
            Function<HikariDataSource, PersistenceUnitInfo> unitProvider,
            @MagicConstant(stringValues = {"update", "validate"}) String hbm2ddl) {
        var config = Map.of(
                "hibernate.connection.driver_class", info.type().getDriverClass().getCanonicalName(),
                "hibernate.connection.url", info.url(),
                "hibernate.connection.username", info.user(),
                "hibernate.connection.password", info.pass(),
                "hibernate.dialect", info.type().getDialectClass().getCanonicalName(),
                "hibernate.show_sql", String.valueOf(Debug.isDebug()),
                "hibernate.hbm2ddl.auto", hbm2ddl
        );
        var dataSource = new HikariDataSource() {{
            setDriverClassName(info.type().getDriverClass().getCanonicalName());
            setJdbcUrl(info.url());
            setUsername(info.user());
            setPassword(info.pass());
        }};
        var unit = unitProvider.apply(dataSource);
        var factory = SPI.createContainerEntityManagerFactory(unit, config);
        var manager = factory.createEntityManager();
        return new Unit(dataSource, manager);
    }

    @Override
    public <T> T save(T object) {
        wrapTransaction(() -> {
            try {
                manager.persist(object);
            } catch (Throwable t) {
                log.debug("persist() failed for " + object, t);
                manager.merge(object);
            }
        });
        return object;
    }

    @Override
    public int delete(Object... infractions) {
        var transaction = manager.getTransaction();
        var c = 0;
        synchronized (transaction) {
            try {
                transaction.begin();
                for (Object each : infractions) {
                    each = manager.merge(each);
                    manager.remove(each);
                    c += 1;
                }
                manager.flush();
                transaction.commit();
            } catch (Throwable t) {
                transaction.rollback();
                LibMod.Resources.printExceptionWithIssueReportUrl(libMod, "Could not remove all entities", t);
            }
        }
        return c;
    }

    @SuppressWarnings("UnusedReturnValue")
    private <R> R wrapQuery(Function<Query, R> executor, Query query) {
        var wrapper = new Runnable() {
            R result;

            @Override
            public void run() {
                result = executor.apply(query);
            }

            @Override
            public String toString() {
                return query.toString();
            }
        };
        wrapTransaction(wrapper);
        return wrapper.result;
    }

    private void wrapTransaction(Runnable task) {
        var transaction = manager.getTransaction();
        synchronized (transaction) {
            transaction.begin();
            try {
                task.run();
                transaction.commit();
            } catch (Throwable t) {
                libMod.log().warn("Could not execute task " + task, t);
                if (transaction.isActive())
                    transaction.rollback();
                throw t;
            }
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
