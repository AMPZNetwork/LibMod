package com.ampznetwork.libmod.api;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import org.comroid.api.attr.Named;
import org.comroid.api.func.util.Command;

import java.util.Set;

public interface SubMod {
    LibMod getLib();

    Command.Manager getCmdr();

    Set<Capability> getCapabilities();

    Class<?> getModuleType();

    Set<Class<? extends DbObject>> getEntityTypes();

    IEntityService getEntityService() throws UnsupportedOperationException;

    default boolean hasCapability(Capability capability) {
        return getCapabilities().contains(capability);
    }

    enum Capability implements Named {
        Database
    }
}
