package com.ampznetwork.libmod.spigot;

import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.comroid.api.func.util.Command;
import org.comroid.api.func.util.Streams;

import java.util.stream.Stream;

public abstract class SpigotPluginBase extends JavaPlugin {
    @Getter
    protected     Command.Manager                cmdr = new Command.Manager();
    @Delegate(types = { TabCompleter.class, CommandExecutor.class })
    private final Command.Manager.Adapter$Spigot adp = cmdr.new Adapter$Spigot(SpigotPluginBase.this) {
        @Override
        public Stream<Object> expandContext(Object... context) {
            return super.expandContext(context).collect(Streams.append(SpigotPluginBase.this));
        }
    };

    @Override
    public void onLoad() {
        cmdr.addChild(this);

        super.onLoad();

        cmdr.initialize();
    }
}
