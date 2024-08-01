package com.ampznetwork.banmod.spigot;

import com.ampznetwork.banmod.spigot.adp.internal.SpigotPlayerAdapter;
import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.comroid.api.func.util.Command;
import org.comroid.api.java.StackTraceUtils;
import org.slf4j.Logger;

import java.util.stream.Stream;

@Getter
@Slf4j(topic = LibMod.Strings.AddonName)
public class LibMod$Spigot extends JavaPlugin implements LibMod {
    static {
        StackTraceUtils.EXTRA_FILTER_NAMES.add("com.ampznetwork");
    }

    private final SpigotPlayerAdapter playerAdapter = new SpigotPlayerAdapter(this);
    private IEntityService entityService;
    private FileConfiguration config;
    private Command.Manager cmdr;
    @Delegate(types = {TabCompleter.class, CommandExecutor.class})
    private Command.Manager.Adapter$Spigot adapter;

    @Override
    public Logger log() {
        return log;
    }

    @Override
    public void onLoad() {
        if (!getServer().getOnlineMode())
            log.warn("Offline mode is not fully supported! Players can rejoin even after being banned.");

        saveDefaultConfig();
        this.config = super.getConfig();

        this.cmdr = new Command.Manager() {{
            this.<Command.ContextProvider>addChild($ -> Stream.of(BanMod$Spigot.this));
            this.addChild(Command.PermissionChecker.minecraft(playerAdapter));
        }};
        this.adapter = cmdr.new Adapter$Spigot(this);
        cmdr.register(BanModCommands.class);
        cmdr.register(this);
        cmdr.initialize();
    }

    @Override
    @SneakyThrows
    public void onEnable() {
        var db = getDatabaseInfo();
        this.entityService = switch (db.impl()) {
            case FILE -> new LocalEntityService(this);
            case DATABASE -> new HibernateEntityService(this);
        };

        defaultCategory = entityService.defaultCategory();

        Bukkit.getPluginManager().registerEvents(eventDispatch, this);
    }

    @Override
    @SneakyThrows
    public void onDisable() {
        this.entityService.terminate();
    }

    @Override
    public void reload() {
        try {
            onDisable();
        } catch (Throwable ignored) {
        }
        reloadConfig();
        config = getConfig();
        onEnable();
    }

    @Override
    public DatabaseInfo getDatabaseInfo() {
        var dbImpl = IEntityService.Type.valueOf(config.getString("banmod.entity-service", "database").toUpperCase());
        var dbType = IEntityService.DatabaseType.valueOf(config.getString("banmod.database.type", "h2"));
        var dbUrl = config.getString("banmod.database.url", "jdbc:h2:file:./BanMod.h2");
        var dbUser = config.getString("banmod.database.username", "sa");
        var dbPass = config.getString("banmod.database.password", "");
        return new DatabaseInfo(dbImpl, dbType, dbUrl, dbUser, dbPass);
    }
}
