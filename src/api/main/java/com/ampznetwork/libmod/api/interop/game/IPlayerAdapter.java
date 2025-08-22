package com.ampznetwork.libmod.api.interop.game;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.adapter.BookAdapter;
import com.ampznetwork.libmod.api.entity.Player;
import net.kyori.adventure.text.Component;
import org.comroid.api.data.Vector;
import org.comroid.commands.model.permission.MinecraftPermissionAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface IPlayerAdapter extends MinecraftPermissionAdapter, PlayerIdentifierAdapter {
    LibMod getLib();

    @Override
    default PlayerFactory getPlayerFactory() {
        return (id, name) -> getLib().getEntityService().getAccessor(Player.TYPE)
                .getOrCreate(id)
                .setUpdateOriginal(player -> player.setName(name))
                .complete(builder -> builder.name(name));
    }

    @Override
    default Optional<Player> getPlayer(UUID playerId) {
        return getCurrentPlayers()
                .filter(plr -> plr.getId().equals(playerId))
                .findAny()
                .or(() -> getLib().getEntityService().getAccessor(Player.TYPE).get(playerId));
    }

    @Override
    default Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(getIdOrThrow(name)).flatMap(this::getPlayer);
    }

    Stream<Player> getCurrentPlayers();

    String getDisplayName(UUID playerId);

    default Optional<UUID> getId(String name) {
        try {
            return Optional.of(getIdOrThrow(name));
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }

    default UUID getIdOrThrow(String name) {
        return Player.fetchId(name).join();
    }

    default String getName(UUID playerId) {
        return getLib().getEntityService()
                .getAccessor(Player.TYPE)
                .getOrCreate(playerId).get()
                .getOrFetchUsername().join();
    }

    boolean isOnline(UUID playerId);

    String getWorldName(UUID playerId);

    Vector.N3 getPosition(UUID playerId);

    void kick(UUID playerId, Component reason);

    void send(UUID playerId, Component component);

    void broadcast(@Nullable String recieverPermission, Component component);

    void openBook(Player player, BookAdapter book);

    Optional<UUID> getIdFromNativePlayer(Object nativePlayer);

    default Optional<Player> convertNativePlayer(Object nativePlayer) {
        return nativePlayer instanceof Player player ? Optional.of(player) : getIdFromNativePlayer(nativePlayer).flatMap(this::getPlayer);
    }
}
