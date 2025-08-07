package com.ampznetwork.libmod.spigot;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.Player;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.messaging.MessagingService;
import com.ampznetwork.libmod.api.messaging.NotifyEvent;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import com.ampznetwork.libmod.api.util.Util;
import com.ampznetwork.libmod.core.database.hibernate.HibernateEntityService;
import com.ampznetwork.libmod.spigot.adapter.SpigotPlayerAdapter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.comroid.api.Polyfill;
import org.comroid.api.java.StackTraceUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class LibMod$Spigot extends SubMod$Spigot implements LibMod {
    static {
        StackTraceUtils.EXTRA_FILTER_NAMES.add("com.ampznetwork");
    }

    List<SubMod>             registeredSubMods = new ArrayList<>();
    SpigotPlayerAdapter      playerAdapter     = new SpigotPlayerAdapter(this);
    ScheduledExecutorService scheduler         = Executors.newScheduledThreadPool(4);
    @NonFinal LuckPerms luckPerms;

    public LibMod$Spigot() {
        super(Set.of(Capability.Database), new HashSet<>() {{add(Player.class);}});
        if (getMessagingServiceConfig() instanceof MessagingService.PollingDatabase) entityTypes.add(NotifyEvent.class);
    }

    @Override
    public DatabaseInfo getDatabaseInfo() {
        return getDatabaseInfo(getConfig().getConfigurationSection("database"),
                LibMod.Resources.DefaultDbType,
                Resources.DefaultDbUrl,
                Resources.DefaultDbUsername,
                Resources.DefaultDbPassword);
    }

    @Override
    public String getMessagingServiceTypeName() {
        return getConfig().getString("messaging-service.type", Resources.DefaultMessagingServiceType);
    }

    @Override
    public @Nullable MessagingService.Config getMessagingServiceConfig() {
        var cfg = getConfig();
        switch (getMessagingServiceTypeName()) {
            case "none":
                return null;
            case "polling-db":
                var interval = Polyfill.parseDuration(cfg.getString("messaging-service.interval", "2s"));
                var dbInfo = getDatabaseInfo(cfg.getConfigurationSection("messaging-service.database"),
                        "MySQL",
                        null,
                        "anonymous",
                        "anonymous");
                return new MessagingService.PollingDatabase.Config(dbInfo, interval);
            case "rabbit-mq":
                return new MessagingService.RabbitMQ.Config(cfg.getString("messaging-service.uri",
                        "amqp://anonymous:anonymous@localhost:5672/messaging"));
            default:
                throw new UnsupportedOperationException("Unknown messaging service type: " + getMessagingServiceTypeName());
        }
    }

    @Override
    public String getServerName() {
        return Util.Kyori.sanitize(getConfig().getString("server.name"));
    }

    @Override
    public void register(SubMod mod) {
        registeredSubMods.add(mod);
    }

    @Override
    public Stream<String> worldNames() {
        return Bukkit.getWorlds().stream().map(World::getName);
    }

    @Override
    public Stream<String> materials() {
        return Arrays.stream(Material.values()).map(mat -> mat.getKey().toString());
    }

    @Override
    public Stream<String> entityTypes() {
        return Arrays.stream(EntityType.values()).map(typ -> typ.getKey().toString());
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lib = this;
    }

    @Override
    public void onEnable() {
        luckPerms = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(LuckPerms.class)).getProvider();

        super.onEnable();
    }

    @Override
    protected HibernateEntityService createEntityService() {
        return createHibernate(this,
                LibMod.class,
                registeredSubMods.stream().flatMap(sub -> sub.getEntityTypes().stream()));
    }

    @Contract("null,_,_,_,_ -> null; !null,_,_,_,_ -> new")
    private DatabaseInfo getDatabaseInfo(
            @Nullable ConfigurationSection config, String defType, String defUrl,
            String defUser, String defPass
    ) {
        if (config == null) return null;
        var dbType = IEntityService.DatabaseType.valueOf(config.getString("type", defType));
        var dbUrl  = config.getString("url", defUrl);
        var dbUser = config.getString("username", defUser);
        var dbPass = config.getString("password", defPass);
        return new DatabaseInfo(dbType, dbUrl, dbUser, dbPass);
    }
}
