package com.ampznetwork.libmod.api;

import com.ampznetwork.libmod.api.adapter.SubMod;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.interop.game.IPlayerAdapter;
import lombok.experimental.UtilityClass;

import java.util.Collection;

public interface LibMod {
    Collection<? extends SubMod> getRegisteredSubMods();

    IPlayerAdapter getPlayerAdapter();

    IEntityService getEntityService();

    void register(SubMod mod);

    @UtilityClass
    final class Strings {
        public static final String AddonName = "LibMod";
        public static final String AddonId   = "libmod";
    }
}
