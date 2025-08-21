package com.ampznetwork.libmod.spigot;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.api.model.config.DatabaseConfigAdapter;
import com.ampznetwork.libmod.api.util.chat.BroadcastWrapper;
import com.ampznetwork.libmod.core.database.hibernate.HibernateEntityService;
import com.ampznetwork.libmod.core.database.hibernate.PersistenceUnitBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.comroid.api.func.util.Streams;
import org.comroid.commands.Command;
import org.comroid.commands.impl.CommandUsage;
import org.comroid.commands.model.CommandError;
import org.comroid.commands.model.CommandPrivacyLevel;
import org.comroid.commands.model.permission.PermissionChecker;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class SubMod$Spigot extends SpigotPluginBase implements SubMod, PermissionChecker {
    protected           Set<Capability> capabilities;
    protected           Set<Class<? extends DbObject>> entityTypes;
    protected @NonFinal LibMod$Spigot   lib;
    protected @NonFinal BroadcastWrapper chat;
    protected @NonFinal IEntityService  entityService;

    @Override
    public String getConfigDir() {
        return "plugins/" + getName();
    }

    @Override
    public final void executeSync(Runnable task) {
        Bukkit.getScheduler().runTask(this, task);
    }

    @Override
    @MustBeInvokedByOverriders
    public void onLoad() {
        this.lib = getPlugin(LibMod$Spigot.class);

        super.onLoad();
        saveDefaultConfig();

        lib.register(this);
        this.chat = new BroadcastWrapper(getThemeColor(), lib, getName());
    }

    @Override
    @MustBeInvokedByOverriders
    public void onDisable() {
        super.onDisable();

        if ((this instanceof LibMod || !lib.getEntityService()
                .equals(entityService)) && entityService != null) entityService.close();
    }

    @Override
    @MustBeInvokedByOverriders
    public void onEnable() {
        super.onEnable();

        this.entityService = createEntityService();
    }

    protected IEntityService createEntityService() {
        return this instanceof DatabaseConfigAdapter && getDatabaseInfo() != null ? createHibernate(this,
                getModuleType(),
                Stream.concat(lib.getEntityTypes().stream(), entityTypes.stream())) : lib.getEntityService();
    }

    @Override
    public BroadcastWrapper chat() {
        return chat;
    }

    @Override
    public boolean userHasPermission(CommandUsage usage, Object key) {
        var userId = usage.getContext()
                .stream()
                .flatMap(Streams.cast(UUID.class))
                .findAny()
                .orElseThrow(() -> new CommandError("Could not find User"));
        return lib.getLuckPerms()
                .getPlayerAdapter(Player.class)
                .getPermissionData(Objects.requireNonNull(Bukkit.getPlayer(userId),
                        "Unexpected state: Player is offline " + userId))
                .checkPermission(key.toString())
                .asBoolean();
    }

    @Command(privacy = CommandPrivacyLevel.PRIVATE)
    public Object reload() {
        onDisable();
        reloadConfig();
        onEnable();
        return "Reload complete!";
    }

    protected static HibernateEntityService createHibernate(
            SubMod mod, Class<?> modClass,
            Stream<Class<?>> entityTypes
    ) {
        return new HibernateEntityService(mod,
                dataSource -> new PersistenceUnitBase(modClass.getSimpleName() + " shared Database",
                        modClass,
                        dataSource,
                        entityTypes.toArray(Class[]::new)));
    }
}
