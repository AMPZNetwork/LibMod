package com.ampznetwork.libmod.api;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import org.comroid.api.attr.Named;
import org.comroid.api.func.Specifiable;
import org.comroid.api.func.util.Command;
import org.comroid.api.func.util.Streams;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface SubMod extends Specifiable<SubMod>, Command.ContextProvider {
    LibMod getLib();

    Command.Manager getCmdr();

    Set<Capability> getCapabilities();

    Class<?> getModuleType();

    Set<Class<? extends DbObject>> getEntityTypes();

    IEntityService getEntityService() throws UnsupportedOperationException;

    void executeSync(Runnable task);

    default boolean hasCapability(Capability capability) {
        return getCapabilities().contains(capability);
    }

    @Override
    default Stream<Object> expandContext(Object... context) {
        return Arrays.stream(context)
                .flatMap(Streams.expand(it -> it instanceof UUID id ? getLib().getPlayerAdapter().getPlayer(id).stream() : Stream.empty()));
    }

    enum Capability implements Named {
        Database
    }
}
