package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.model.EntityType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.comroid.api.attr.Named;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MySQLDialect;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IEntityService {
    <T extends DbObject, B extends DbObject.Builder<T, B>> EntityAccessor<T, B> getAccessor(EntityType<T, B> type);

    <T extends DbObject> T save(T object);

    void refresh(EntityType<?, ?> type, UUID... ids);

    void uncache(Object id, @Nullable DbObject obj);

    int delete(Object... objects);

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    enum DatabaseType implements Named {
        h2(org.h2.Driver.class, H2Dialect.class),
        MySQL(com.mysql.jdbc.Driver.class, MySQLDialect.class),
        MariaDB(org.mariadb.jdbc.Driver.class, MariaDBDialect.class);

        Class<?> driverClass;
        Class<?> dialectClass;
    }
}