package com.ampznetwork.libmod.api.adapter;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import org.comroid.api.attr.Named;
import org.comroid.api.func.util.Command;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.Set;

public interface SubMod {
    LibMod getLib();

    Command.Manager getCmdr();

    Set<Capability> getCapabilities();

    Set<Class<? extends DbObject>> getEntityTypes();

    IEntityService getEntityService() throws UnsupportedOperationException;

    default boolean hasCapability(Capability capability) {
        return getCapabilities().contains(capability);
    }

    PersistenceUnitInfo createPersistenceUnit(DataSource dataSource);

    enum Capability implements Named {
        Database
    }
}
