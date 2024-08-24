package com.ampznetwork.libmod.fabric.adp;

import com.ampznetwork.libmod.api.adapter.BookAdapter;
import com.ampznetwork.libmod.api.entity.Player;
import com.ampznetwork.libmod.api.interop.game.IPlayerAdapter;
import com.ampznetwork.libmod.fabric.LibMod$Fabric;
import io.netty.buffer.Unpooled;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.comroid.api.data.Vector;
import org.comroid.api.func.util.Command;
import org.comroid.api.net.REST;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.*;

@Value
@Log4j2
public class FabricPlayerAdapter implements IPlayerAdapter {
    LibMod$Fabric lib;

    @Override
    public Stream<Player> getCurrentPlayers() {
        return lib.getServer().getPlayerManager()
                .getPlayerList().stream()
                .map(spe -> lib.getEntityService().getAccessor(Player.TYPE)
                        .getOrCreate(spe.getUuid())
                        .setUpdateOriginal(player -> player.setName(spe.getName().getString()))
                        .complete(builder -> builder.name(spe.getName().getString()).id(spe.getUuid())));
    }

    @Override
    public String getName(UUID playerId) {
        if (isOnline(playerId))
            return lib.getServer().getPlayerManager()
                    .getPlayer(playerId)
                    .getName().getString();
        return REST.get("https://sessionserver.mojang.com/session/minecraft/profile/" + playerId)
                .thenApply(REST.Response::validate2xxOK)
                .thenApply(rsp -> rsp.getBody().get("name").asString())
                .exceptionally(t -> {
                    log.warn("Could not retrieve Minecraft Username for user {}", playerId, t);
                    return "Steve";
                }).join();
    }

    @Override
    public boolean isOnline(UUID playerId) {
        return lib.getServer().getPlayerManager()
                       .getPlayer(playerId) != null;
    }

    @Override
    public String getWorldName(UUID playerId) {
        return lib.getServer().getPlayerManager()
                .getPlayer(playerId).getWorld()
                .getRegistryKey().getValue()
                .toString();
    }

    @Override
    public Vector.N3 getPosition(UUID playerId) {
        var vec = lib.getServer().getPlayerManager()
                .getPlayer(playerId).getPos();
        return new Vector.N3(vec.x, vec.y, vec.z);
    }

    @Override
    public void kick(UUID playerId, Component reason) {
        var serialize = LibMod$Fabric.component2text(reason);
        Optional.ofNullable(lib.getServer())
                .map(MinecraftServer::getPlayerManager)
                .map(manager -> manager.getPlayer(playerId))
                .orElseThrow(() -> new Command.Error("Player not found"))
                .networkHandler
                .disconnect(serialize);
    }

    @Override
    public void send(UUID playerId, Component component) {
        var serialize = LibMod$Fabric.component2text(component);
        var player    = lib.getServer().getPlayerManager().getPlayer(playerId);
        if (player == null) return;
        player.sendMessage(serialize);
    }

    @Override
    public void broadcast(@Nullable String recieverPermission, Component component) {
        var serialize = LibMod$Fabric.component2text(component);
        lib.getServer().getPlayerManager()
                .getPlayerList().stream()
                .filter(player -> recieverPermission == null
                                  || checkPermission(player.getUuid(), recieverPermission)
                                          .toBooleanOrElse(false))
                .forEach(player -> player.sendMessage(serialize));
    }

    @Override
    public void openBook(UUID playerId, BookAdapter book) {
        var plr = lib.getServer().getPlayerManager()
                .getPlayer(playerId);
        ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

        // Set the stack's title, author, and pages
        var tag = new NbtCompound();
        tag.putString("title", BookAdapter.TITLE);
        tag.putString("author", BookAdapter.AUTHOR);

        var pages = book.getPages().stream()
                .map(page -> {
                    var text = text();
                    for (var comp : page)
                        text.append(comp);
                    return text.build();
                })
                .map(page -> NbtString.of(gson().serialize(page)))
                .collect(NbtList::new, Collection::add, Collection::addAll);

        tag.put("pages", pages);
        stack.setNbt(tag);

        // Create a PacketByteBuf and write the stack item stack to it
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeItemStack(stack);

        ServerPlayNetworking.send(plr, new Identifier("minecraft", "book_open"), buf);
    }

    @Override
    public boolean checkOpLevel(UUID playerId, @MagicConstant(intValues = { 0, 1, 2, 3, 4 }) int minimum) {
        return Optional.of(lib.getServer())
                .map(MinecraftServer::getPlayerManager)
                .map(pm -> pm.getPlayer(playerId))
                .filter(spe -> spe.hasPermissionLevel(minimum))
                .isPresent();
    }

    @Override
    public TriState checkPermission(UUID playerId, String key, boolean explicit) {
        return switch (Permissions.getPermissionValue(playerId, key).join()) {
            case FALSE -> TriState.FALSE;
            case DEFAULT -> TriState.NOT_SET;
            case TRUE -> TriState.TRUE;
        };
    }
}
