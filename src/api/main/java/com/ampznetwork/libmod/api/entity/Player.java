package com.ampznetwork.libmod.api.entity;

import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.convert.UuidVarchar36Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.comroid.api.Polyfill;
import org.comroid.api.net.REST;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static org.comroid.api.net.REST.Method.*;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "playerdata")
public class Player extends DbObject {
    /*
    watch me literally not give a single fuck at this point.
    i've spent years and years trying to find a good solution to instantiate a self-referencing type generic
    but they are always impossible to compile.

    so now, fuck that shit and we're abusing Polyfill.uncheckedCast().
    fuck you, java. fix your type generics.
     */
    public static final EntityType<Player, Builder<Player, ?>> TYPE
            = Polyfill.uncheckedCast(new EntityType<>(Player::builder, null, Player.class, Builder.class));
    public static       BiConsumer<UUID, String>    CACHE_NAME = null;

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
