package com.ampznetwork.libmod.fabric;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.messaging.MessagingService;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import com.ampznetwork.libmod.fabric.adp.FabricPlayerAdapter;
import com.ampznetwork.libmod.fabric.config.Config;
import com.ampznetwork.libmod.fabric.config.LibModConfig;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import lombok.Value;
import lombok.experimental.NonFinal;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.comroid.api.Polyfill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value
public class LibMod$Fabric extends SubMod$Fabric implements LibMod, ModInitializer, ServerLifecycleEvents.ServerStarting {
    public static LibMod INSTANCE;

    public static Text component2text(Component component) {
        var json = GsonComponentSerializer.gson().serializeToTree(component);
        return TextCodecs.STRINGIFIED_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
    }

    List<SubMod>        registeredSubMods = new ArrayList<>();
    FabricPlayerAdapter playerAdapter = new FabricPlayerAdapter(this);
    LibModConfig        config        = Config.createAndLoad(LibModConfig.class);
    @NonFinal MinecraftServer server;

    {
        INSTANCE = this;
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
                        "amqp://anonymous:anonymous@localhost:5672/banmod_messaging"));
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
}
