package com.ampznetwork.libmod.spigot;

import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.comroid.api.java.StackTraceUtils;
import org.comroid.commands.impl.CommandManager;
import org.comroid.commands.impl.minecraft.SpigotCommandAdapter;

public abstract class SpigotPluginBase extends JavaPlugin {
    static {
        StackTraceUtils.EXTRA_FILTER_NAMES.add("com.ampznetwork");
    }

    @Getter protected CommandManager       cmdr = new CommandManager();
    @Delegate(types = {
            TabCompleter.class, CommandExecutor.class
    }) private final  SpigotCommandAdapter adp  = new SpigotCommandAdapter(cmdr, SpigotPluginBase.this);

    @Override
    public void onLoad() {
        cmdr.addChild(this);

        super.onLoad();

        cmdr.initialize();
    }
}
