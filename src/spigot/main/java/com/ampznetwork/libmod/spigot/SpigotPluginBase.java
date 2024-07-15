package com.ampznetwork.libmod.spigot;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.addon.Mod;
import com.ampznetwork.libmod.api.addon.Registry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.comroid.api.func.util.Command;
import org.slf4j.Logger;

@Getter
@Slf4j
public abstract class SpigotPluginBase extends JavaPlugin implements Mod.Spigot {
    private final Registry registry;
    private final Command.Manager.Adapter$Spigot commandAdapter;

    public SpigotPluginBase(Registry registry) {
        this.registry = registry;
        this.commandAdapter = api().getCommandManager().new Adapter$Spigot(this);
    }

    @Override
    public Logger log() {
        return log;
    }

    @Override
    public LibMod api() {
        return this instanceof LibMod lib ? lib : (LibMod) Bukkit.getPluginManager().getPlugin("LibMod");
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void reload() {
        onDisable();
        reloadConfig();
        onEnable();
    }
}
