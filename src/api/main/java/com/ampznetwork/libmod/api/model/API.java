package com.ampznetwork.libmod.api.model;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.EntityAccessor;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.interop.game.IPlayerAdapter;

import java.util.concurrent.ScheduledExecutorService;

public interface API {
    IPlayerAdapter getPlayerAdapter();

    ScheduledExecutorService getScheduler();

    IEntityService getEntityService() throws UnsupportedOperationException;

    default <T extends DbObject, B extends DbObject.Builder<T, ?>> EntityAccessor<T, B> getEntityAccessor(EntityType<T, B> type) {
        return getEntityService().getAccessor(type);
    }

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
