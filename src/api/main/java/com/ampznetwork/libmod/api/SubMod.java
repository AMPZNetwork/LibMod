package com.ampznetwork.libmod.api;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.model.API;
import com.ampznetwork.libmod.api.model.config.DatabaseConfigAdapter;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import com.ampznetwork.libmod.api.util.chat.BroadcastWrapper;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.comroid.api.attr.Named;
import org.comroid.api.func.util.Command;
import org.comroid.api.func.util.Streams;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface SubMod
        extends DatabaseConfigAdapter, BroadcastWrapper.Delegate, Command.ContextProvider, API.Delegate, Named {
    LibMod getLib();

    @Override
    @Nullable
    default DatabaseInfo getDatabaseInfo() {
        return null;
    }

    @Override
    default String getName() {
        return getClass().getSimpleName().split("[.$]")[0];
    }

    Command.Manager getCmdr();

    Set<Capability> getCapabilities();

    default Class<?> getModuleType() {
        return getClass();
    }

    Set<Class<? extends DbObject>> getEntityTypes();

    default TextColor getThemeColor() {
        return NamedTextColor.AQUA;
    }

    String getConfigDir();

    void executeSync(Runnable task);

    default boolean hasCapability(Capability capability) {
        return getCapabilities().contains(capability);
    }

    @Override
    default Stream<Object> expandContext(Object... context) {
        return Arrays.stream(context)
                .flatMap(Streams.expand(it -> it instanceof UUID id
                                              ? getLib().getPlayerAdapter().getPlayer(id).stream()
                                              : Stream.empty()));
    }

    default <T extends SubMod> T sub(Class<T> type) {
        return getLib().getRegisteredSubMods().stream().flatMap(Streams.cast(type)).findAny().orElse(null);
    }

    enum Capability implements Named {
        Database
    }
}
