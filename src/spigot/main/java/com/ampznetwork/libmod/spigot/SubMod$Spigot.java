package com.ampznetwork.libmod.spigot;

import com.ampznetwork.banmod.spigot.LibMod$Spigot;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.core.database.hibernate.HibernateEntityService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

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
}
