package com.ampznetwork.libmod.spigot;

import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.comroid.api.func.util.Command;

public class SpigotPluginBase extends JavaPlugin {
    @Getter
    protected     Command.Manager                cmdr = new Command.Manager();
    @Delegate(types = { TabCompleter.class, CommandExecutor.class })
    private final Command.Manager.Adapter$Spigot adp  = cmdr.new Adapter$Spigot(SpigotPluginBase.this);

    @Override
    public void onLoad() {
        super.onLoad();

        cmdr.initialize();
    }
}
