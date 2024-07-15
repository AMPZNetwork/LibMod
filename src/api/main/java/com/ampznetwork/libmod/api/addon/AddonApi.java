package com.ampznetwork.libmod.api.addon;

import com.ampznetwork.libmod.api.adapter.IEntityService;
import com.ampznetwork.libmod.api.adapter.IPlayerAdapter;
import com.ampznetwork.libmod.api.display.IDisplayAdapter;
import org.comroid.api.func.util.Command;

public interface AddonApi {
    IEntityService getEntityService();

    IPlayerAdapter getPlayerAdapter();

    IDisplayAdapter<?> getDisplayAdapter();

    Command.Manager getCommandManager();

    interface Spigot extends AddonApi {
        @Override
        IDisplayAdapter.Spigot getDisplayAdapter();
    }

    interface Fabric extends AddonApi {
        @Override
        IDisplayAdapter.Fabric getDisplayAdapter();
    }

    interface Forge extends AddonApi {
        @Override
        IDisplayAdapter.Forge getDisplayAdapter();
    }
}
