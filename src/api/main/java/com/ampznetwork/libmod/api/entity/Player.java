package com.ampznetwork.libmod.api.entity;

import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.convert.UuidVarchar36Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.comroid.api.net.REST;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static org.comroid.api.net.REST.Method.GET;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "libmod_players")
public class Player extends DbObject.ByUuid {
    public static final EntityType<UUID, Player, Builder> TYPE
            = new EntityType<>(Player::builder, null, Player.class, Builder.class);
    public static       BiConsumer<UUID, String>          CACHE_NAME = null;

    public static CompletableFuture<UUID> fetchId(String name) {
        var future = REST.get("https://api.mojang.com/users/profiles/minecraft/" + name)
                .thenApply(REST.Response::validate2xxOK)
                .thenApply(rsp -> rsp.getBody().get("id").asString())
                .thenApply(UuidVarchar36Converter::fillDashes)
                .thenApply(UUID::fromString);
        future.thenAccept(id -> CACHE_NAME.accept(id, name));
        return future;
    }

    public static CompletableFuture<String> fetchUsername(UUID id) {
        var future = REST.request(GET, "https://sessionserver.mojang.com/session/minecraft/profile/" + id).execute()
                .thenApply(REST.Response::validate2xxOK)
                .thenApply(rsp -> rsp.getBody().get("name").asString());
        future.thenAccept(name -> CACHE_NAME.accept(id, name));
        return future;
    }

    private @Nullable String name;

    @Transient
    public CompletableFuture<String> getOrFetchUsername() {
        return Optional.ofNullable(name)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> fetchUsername(id));
    }
}
