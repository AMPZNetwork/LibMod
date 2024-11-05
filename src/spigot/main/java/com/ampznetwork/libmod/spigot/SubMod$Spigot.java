package com.ampznetwork.libmod.spigot;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.core.database.hibernate.HibernateEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.comroid.api.func.util.Command;
import org.comroid.api.func.util.Streams;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class SubMod$Spigot extends SpigotPluginBase implements SubMod, Command.PermissionChecker {
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

        if ((this instanceof LibMod || !lib.getEntityService().equals(entityService)) && entityService != null)
            entityService.close();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (lib != null)
            this.entityService = lib.getEntityService();
    }

    @Override
    public final void executeSync(Runnable task) {
        Bukkit.getScheduler().runTask(this, task);
    }

    @Override
    public boolean userHasPermission(Command.Usage usage, Object key) {
        var userId = usage.getContext().stream()
                .flatMap(Streams.cast(UUID.class))
                .findAny().orElseThrow();
        return lib.getLuckPerms()
                .getPlayerAdapter(Player.class)
                .getPermissionData(Objects.requireNonNull(Bukkit.getPlayer(userId), "Unexpected state: Player is offline " + userId))
                .checkPermission(key.toString())
                .asBoolean();
    }
}
