package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.model.EntityType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.comroid.api.attr.Named;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MySQL8Dialect;
import org.jetbrains.annotations.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.UUID;
import java.util.function.Function;

public interface IEntityService {
    LibMod getLib();

    <T extends DbObject, B extends DbObject.Builder<T, ?>> EntityAccessor<T, B> getAccessor(EntityType<T, ? super B> type);

    <T extends DbObject> T save(T object);

    void refresh(EntityType<?, ?> type, UUID... ids);

    void uncache(UUID id, @Nullable DbObject obj);

    int delete(@SuppressWarnings("rawtypes") DbObject... objects);

    Query createQuery(Function<EntityManager,Query>factory);

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    enum DatabaseType implements Named {
        h2(org.h2.Driver.class, H2Dialect.class),
        MySQL(com.mysql.jdbc.Driver.class, MySQL8Dialect.class),
        MariaDB(org.mariadb.jdbc.Driver.class, MariaDBDialect.class);

        Class<?> driverClass;
        Class<?> dialectClass;
    }
}
