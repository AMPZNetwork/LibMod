package com.ampznetwork.libmod.api.model;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.interop.game.IPlayerAdapter;

import java.util.concurrent.ScheduledExecutorService;

public interface API {
    IPlayerAdapter getPlayerAdapter();

    ScheduledExecutorService getScheduler();

    IEntityService getEntityService() throws UnsupportedOperationException;

    interface Delegate extends API {
        LibMod getLib();

        default IPlayerAdapter getPlayerAdapter() {
            return getLib().getPlayerAdapter();
        }

        default ScheduledExecutorService getScheduler() {
            return getLib().getScheduler();
        }

        default IEntityService getEntityService() throws UnsupportedOperationException {
            return getLib().getEntityService();
        }
    }
}
