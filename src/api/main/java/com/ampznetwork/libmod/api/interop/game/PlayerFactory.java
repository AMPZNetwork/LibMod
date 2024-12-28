package com.ampznetwork.libmod.api.interop.game;

import com.ampznetwork.libmod.api.entity.Player;

import java.util.UUID;

public interface PlayerFactory {
    Player createPlayer(UUID id, String name);
}
