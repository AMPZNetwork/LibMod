package com.ampznetwork.libmod.spigot;

import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.core.database.hibernate.HibernateEntityService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.Bukkit;

import java.util.Set;

@Value
@NonFinal
@RequiredArgsConstructor
public abstract class SubMod$Spigot extends SpigotPluginBase implements SubMod {
    protected           Set<Capability>                capabilities;
    protected           Set<Class<? extends DbObject>> entityTypes;
    protected @NonFinal LibMod$Spigot                  lib;
    protected @NonFinal HibernateEntityService         entityService;

    @Override
    public void onLoad() {
        super.onLoad();

        saveDefaultConfig();

        this.lib = getPlugin(LibMod$Spigot.class);
        lib.register(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        entityService.close();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.entityService = new HibernateEntityService(lib, this);
    }

    @Override
    public final void executeSync(Runnable task) {
        Bukkit.getScheduler().runTask(this, task);
    }
}
