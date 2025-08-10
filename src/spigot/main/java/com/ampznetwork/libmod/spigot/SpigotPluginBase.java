package com.ampznetwork.libmod.spigot;

import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.comroid.api.func.util.Command;
import org.comroid.api.func.util.DelegateStream;
import org.comroid.api.java.StackTraceUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.stream.Stream;

public abstract class SpigotPluginBase extends JavaPlugin {
    static {
        StackTraceUtils.EXTRA_FILTER_NAMES.add("com.ampznetwork");
    }

    @Getter
    protected     Command.Manager                cmdr = new Command.Manager();
    @Delegate(types = { TabCompleter.class, CommandExecutor.class })
    private final Command.Manager.Adapter$Spigot adp = cmdr.new Adapter$Spigot(SpigotPluginBase.this) {
        @Override
        public String handleThrowable(Throwable throwable) {
            try (
                    var str = new StringWriter();
                    var ds = new DelegateStream.Output(str);
                    var out = new PrintStream(ds);
            ) {
                StackTraceUtils.writeFilteredStacktrace(throwable, out);
                return ChatColor.RED + str.toString();
            } catch (IOException e) {
                return ChatColor.DARK_RED + "Internal error (please report) " + StackTraceUtils.toString(e);
            }
        }
    };

    @Override
    public void onLoad() {
        cmdr.<Command.ContextProvider>addChild(ctx -> Stream.of(this));
        cmdr.addChild(this);

        super.onLoad();

        cmdr.initialize();
    }
}
