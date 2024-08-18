package com.ampznetwork.libmod.fabric;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.messaging.MessagingService;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import com.ampznetwork.libmod.fabric.adp.internal.FabricPlayerAdapter;
import lombok.Value;
import lombok.experimental.NonFinal;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

@Value
public class LibMod$Fabric extends SubMod$Fabric implements LibMod, ModInitializer, ServerLifecycleEvents.ServerStarting {
    public static LibMod INSTANCE;

    public static Text component2text(Component component) {
        return Text.Serializer.fromJson(gson().serialize(component));
    }
    List<SubMod>        registeredSubMods = new ArrayList<>();
    FabricPlayerAdapter playerAdapter     = new FabricPlayerAdapter(this);
    @NonFinal MinecraftServer server;

    {
        INSTANCE = this;
    }

    @Override
    public DatabaseInfo getDatabaseInfo() {
        return null; // todo
    }

    @Override
    public void register(SubMod mod) {
        registeredSubMods.add(mod);
    }

    @Override
    public String getMessagingServiceTypeName() {
        return null; // todo
    }

    @Override
    public MessagingService.Config getMessagingServiceConfig() {
        return null; // todo
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
