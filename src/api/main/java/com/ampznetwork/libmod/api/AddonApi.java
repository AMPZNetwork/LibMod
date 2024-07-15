package com.ampznetwork.libmod.api;

import com.ampznetwork.libmod.api.database.EntityService;
import com.ampznetwork.libmod.api.display.DisplayAdapter;
import com.ampznetwork.libmod.api.model.adp.PlayerAdapter;
import org.comroid.api.func.util.Command;

public interface AddonApi {
    EntityService getEntityService();

    PlayerAdapter getPlayerAdapter();

    DisplayAdapter<?> getDisplayAdapter();

    Command.Manager getCommandManager();

    interface Spigot extends AddonApi {
        @Override
        DisplayAdapter.Spigot getDisplayAdapter();
    }
}
