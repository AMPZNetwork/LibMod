package com.ampznetwork.libmod.spigot;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import com.ampznetwork.libmod.core.command.LibModCommands;
import com.ampznetwork.libmod.core.database.file.LocalEntityService;
import com.ampznetwork.libmod.core.hibernate.HibernateEntityService;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.comroid.api.func.util.Command;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.stream.Stream;

@Getter
@Slf4j(topic = LibMod.Strings.AddonName)
public class LibMod$Spigot extends SpigotPluginBase implements LibMod {
    private FileConfiguration config;
    private Command.Manager commandManager;
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
            this.<Command.ContextProvider>addChild($ -> Stream.of(LibMod$Spigot.this));
            this.addChild(Command.PermissionChecker.minecraft(playerAdapter));
        }};
        this.adapter = cmdr.new Adapter$Spigot(this);
        cmdr.register(LibModCommands.class);
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
    public @Nullable String getBanAppealUrl() {
        var url = getConfig().get("libmod.appealUrl", null);
        var txt = url == null ? null : url.toString();
        if (txt != null && txt.isBlank()) txt = null;
        return txt;
    }

    @Override
    public boolean allowUnsafeConnections() {
        return config.getBoolean("libmod.allow-unsafe-connections", false);
    }

    @Override
    public DatabaseInfo getDatabaseInfo() {
        var dbImpl = EntityService.Type.valueOf(config.getString("libmod.entity-service", "database").toUpperCase());
        var dbType = EntityService.DatabaseType.valueOf(config.getString("libmod.database.type", "h2"));
        var dbUrl = config.getString("libmod.database.url", "jdbc:h2:file:./LibMod.h2");
        var dbUser = config.getString("libmod.database.username", "sa");
        var dbPass = config.getString("libmod.database.password", "");
        return new DatabaseInfo(dbImpl, dbType, dbUrl, dbUser, dbPass);
    }
}
