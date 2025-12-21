package com.ampznetwork.libmod.api.interop.game;

import com.ampznetwork.libmod.api.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface PlayerIdentifierAdapter {
    default PlayerFactory getPlayerFactory() {
        return (id, name) -> Player.builder().id(id).name(name).build();
    }

    Optional<Player> getPlayer(UUID playerId);

    Optional<Player> getPlayer(String name);

    default String getDisplayName(UUID playerId) {
        return getPlayer(playerId).map(Player::getName).orElseGet(() -> Player.fetchUsername(playerId).join());
    }
}
