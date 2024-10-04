package com.ampznetwork.libmod.spigot.adp;

import com.ampznetwork.libmod.api.adapter.BookAdapter;
import com.ampznetwork.libmod.api.entity.Player;
import com.ampznetwork.libmod.api.interop.game.IPlayerAdapter;
import com.ampznetwork.libmod.spigot.LibMod$Spigot;
import lombok.Value;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.comroid.api.data.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer.*;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.*;

@Value
public class SpigotPlayerAdapter implements IPlayerAdapter {
    LibMod$Spigot lib;

    @Override
    public Stream<Player> getCurrentPlayers() {
        var service = lib.getEntityService();
        return lib.getServer()
                .getOnlinePlayers().stream()
                .map(player -> service.getAccessor(Player.TYPE)
                        .getOrCreate(player.getUniqueId())
                        .setUpdateOriginal(original -> original.setName(player.getName()))
                        .complete(builder -> builder.name(player.getName())));
    }

    @Override
    public UUID getId(String name) {
        final var fetch = Player.fetchId(name);
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(player -> name.equals(player.getName()))
                .findAny()
                .map(OfflinePlayer::getUniqueId)
                .or(() -> lib.getEntityService()
                        .getAccessor(Player.TYPE).all()
                        .filter(pd -> pd.getName().equals(name))
                        .map(Player::getId)
                        .filter($ -> !fetch.isDone() || fetch.cancel(false))
                        .findAny())
                .orElseGet(fetch::join);
    }

    @Override
    public String getName(UUID playerId) {
        final var fetch = Player.fetchUsername(playerId);
        return Optional.ofNullable(lib.getServer().getOfflinePlayer(playerId).getName())
                .or(() -> lib.getEntityService()
                        .getAccessor(Player.TYPE).get(playerId)
                        .map(Player::getName)
                        .filter($ -> !fetch.isDone() || fetch.cancel(false)))
                .orElseGet(fetch::join);
    }

    @Override
    public boolean isOnline(UUID playerId) {
        return lib.getServer().getPlayer(playerId) != null;
    }

    @Override
    public String getWorldName(UUID playerId) {
        return Objects.requireNonNull(Bukkit.getPlayer(playerId), "Player is offline").getWorld().getName();
    }

    @Override
    public Vector.N3 getPosition(UUID playerId) {
        var loc = Bukkit.getPlayer(playerId).getLocation();
        return new Vector.N3(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public void kick(UUID playerId, Component component) {
        var player = Bukkit.getPlayer(playerId);
        if (player == null)
            return;
        var serialize = legacySection().serialize(component);
        player.kickPlayer(serialize);
    }

    @Override
    public void send(UUID playerId, Component component) {
        var player = lib.getServer().getPlayer(playerId);
        if (player == null) return;
        var serialize = get().serialize(component);
        player.spigot().sendMessage(serialize);
    }

    @Override
    public void broadcast(@Nullable String receiverPermission, Component component) {
        final var serialize = get().serialize(component);
        lib.getServer().getOnlinePlayers().stream()
                .filter(player -> receiverPermission == null || player.hasPermission(receiverPermission))
                .forEach(player -> player.spigot().sendMessage(serialize));
    }

    @Override
    public void openBook(Player player, BookAdapter book) {
        if (!isOnline(player.getId()))
            throw new AssertionError("Target player is not online");
        var stack = new ItemStack(Material.WRITTEN_BOOK, 1);
        var meta  = Objects.requireNonNull((BookMeta) stack.getItemMeta(), "item meta");
        meta.setTitle(BookAdapter.TITLE);
        meta.setAuthor(BookAdapter.AUTHOR);
        meta.spigot().setPages(book.getPages().stream()
                .map(page -> Arrays.stream(page)
                        .map(component -> get().serialize(component))
                        .flatMap(Arrays::stream)
                        .toArray(BaseComponent[]::new))
                .toList());
        stack.setItemMeta(meta);
        lib.getServer().getPlayer(player.getId()).openBook(stack);
    }

    @Override
    public boolean checkOpLevel(UUID playerId, int $) {
        if ($ > 1) Bukkit.getLogger().warning("Spigot API does not properly support validating a certain OP level.");
        var player = lib.getServer().getPlayer(playerId);
        return player != null && player.isOp();
    }

    @Override
    public TriState checkPermission(UUID playerId, String _key, boolean explicit) {
        var key    = _key.endsWith(".*") ? _key.substring(0, _key.length() - 1) : _key;
        var player = lib.getServer().getPlayer(playerId);
        if (player == null)
            return TriState.NOT_SET;
        if (explicit)
            return player.getEffectivePermissions().stream()
                    .filter(info -> info.getPermission().toLowerCase()
                            .startsWith(key.toLowerCase()))
                    .map(info -> info.getValue() ? TriState.TRUE : TriState.FALSE)
                    .findAny()
                    .orElse(TriState.NOT_SET);
        return player.hasPermission(key)
               ? TriState.TRUE
               : player.isPermissionSet(key)
                 ? TriState.FALSE
                 : TriState.NOT_SET;
    }
}
