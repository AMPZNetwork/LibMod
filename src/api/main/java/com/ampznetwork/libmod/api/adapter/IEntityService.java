package com.ampznetwork.libmod.api.adapter;

import com.ampznetwork.libmod.api.addon.Mod;
import com.ampznetwork.libmod.api.addon.ModComponent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.comroid.api.attr.Named;
import org.comroid.api.tree.LifeCycle;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MySQL57Dialect;

@SuppressWarnings("UnusedReturnValue")
public interface IEntityService extends ModComponent, LifeCycle {
    Mod getMod();

    <T> T save(T object);

    int delete(Object... objects);

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    enum DatabaseType implements Named {
        h2(org.h2.Driver.class, H2Dialect.class),
        MySQL(com.mysql.jdbc.Driver.class, MySQL57Dialect.class),
        MariaDB(org.mariadb.jdbc.Driver.class, MariaDBDialect.class);

        Class<?> driverClass;
        Class<?> dialectClass;
    }

}
