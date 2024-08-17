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
    <K, T extends DbObject<K>, B extends DbObject.Builder<K, T, B>> EntityAccessor<K, T, B> getAccessor(EntityType<K, T, B> type);

    <T extends DbObject<?>> T save(T object);

    void refresh(EntityType<UUID, ?, ?> type, UUID... ids);

    <K> void uncache(K id, @Nullable DbObject<K> obj);

    int delete(@SuppressWarnings("rawtypes") DbObject... objects);

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
