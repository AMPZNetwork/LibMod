package com.ampznetwork.libmod.fabric;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.Player;
import com.ampznetwork.libmod.api.messaging.MessagingService;
import com.ampznetwork.libmod.api.messaging.NotifyEvent;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import com.ampznetwork.libmod.fabric.adp.FabricPlayerAdapter;
import com.ampznetwork.libmod.fabric.config.Config;
import com.ampznetwork.libmod.fabric.config.LibModConfig;
import com.ampznetwork.libmod.fabric.ticker.TickerEntity;
import lombok.Value;
import lombok.experimental.NonFinal;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.comroid.api.Polyfill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Value
public class LibModFabric extends SubMod$Fabric implements LibMod, ModInitializer,
        ServerLifecycleEvents.ServerStarting, ServerLifecycleEvents.ServerStarted {
    public static LibModFabric INSTANCE;

    public static Text component2text(Component component) {
        /*1.21.1
        var json = GsonComponentSerializer.gson().serializeToTree(component);
        return TextCodecs.STRINGIFIED_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);*/
        return Text.of(GsonComponentSerializer.gson().serialize(component));
    }
    List<SubMod>             registeredSubMods = new ArrayList<>();
    FabricPlayerAdapter      playerAdapter     = new FabricPlayerAdapter(this);
    LibModConfig             config            = Config.createAndLoad(LibModConfig.class);
    ScheduledExecutorService scheduler         = Executors.newScheduledThreadPool(4);
    @NonFinal MinecraftServer server;
    @NonFinal TickerEntity    ticker;

    {
        INSTANCE = this;
    }

    public LibModFabric() {
        super(Set.of(Capability.Database), Set.of(Player.class, NotifyEvent.class));
    }

    @Override
    public DatabaseInfo getDatabaseInfo() {
        return config.getDatabase();
    }

    @Override
    public void register(SubMod mod) {
        registeredSubMods.add(mod);
    }

    @Override
    public String getMessagingServiceTypeName() {
        return config.getMessagingService().getType();
    }

    @Override
    public MessagingService.Config getMessagingServiceConfig() {
        switch (getMessagingServiceTypeName()) {
            case "polling-db":
                var interval = Polyfill.parseDuration(Objects.requireNonNullElse(config.getMessagingService().getInterval(), "2s"));
                var dbInfo = config.getMessagingService().getDatabase();
                return new MessagingService.PollingDatabase.Config(dbInfo, interval);
            case "rabbit-mq":
                return new MessagingService.RabbitMQ.Config(Objects.requireNonNullElse(config.getMessagingService().getUri(),
                        "amqp://anonymous:anonymous@localhost:5672/messaging"));
            default:
                throw new UnsupportedOperationException("Unknown messaging service type: " + getMessagingServiceTypeName());
        }
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(this);

        super.onInitialize();
    }

    @Override
    public void onServerStarting(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void onServerStarted(MinecraftServer server) {
        //this.ticker = new TickerEntity();
        //server.getOverworld().spawnEntity(ticker);
    }
}
