package com.ampznetwork.libmod.core.adapter;

import com.ampznetwork.libmod.api.entity.Player;
import com.ampznetwork.libmod.api.interop.game.PlayerIdentifierAdapter;
import com.ampznetwork.libmod.api.model.convert.UuidVarchar36Converter;
import org.comroid.api.data.seri.DataNode;
import org.comroid.api.func.util.Debug;
import org.comroid.api.net.REST;

import java.util.Optional;
import java.util.UUID;

public class HeadlessPlayerAdapter implements PlayerIdentifierAdapter {
    public static final HeadlessPlayerAdapter INSTANCE = new HeadlessPlayerAdapter();

    @Override
    public Optional<Player> getPlayer(UUID playerId) {
        try {
            return REST.get("https://sessionserver.mojang.com/session/minecraft/profile/" + playerId)
                    .thenApply(REST.Response::validate2xxOK)
                    .thenApply(REST.Response::getBody)
                    .thenApply(this::parsePlayer)
                    .thenApply(Optional::ofNullable)
                    .join();
        } catch (Throwable t) {
            Debug.log("Could not fetch Player", t);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        try {
            return REST.get("https://api.mojang.com/users/profiles/minecraft/" + name)
                    .thenApply(REST.Response::validate2xxOK)
                    .thenApply(REST.Response::getBody)
                    .thenApply(this::parsePlayer)
                    .thenApply(Optional::ofNullable)
                    .join();
        } catch (Throwable t) {
            Debug.log("Could not fetch Player", t);
            return Optional.empty();
        }
    }

    private Player parsePlayer(DataNode data) {
        var id   = new UuidVarchar36Converter().convertToEntityAttribute(data.get("id").asString());
        var name = data.get("name").asString();
        return getPlayerFactory().createPlayer(id, name);
    }
}
