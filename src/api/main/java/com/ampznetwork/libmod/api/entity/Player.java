package com.ampznetwork.libmod.api.entity;

import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.convert.UuidVarchar36Converter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.comroid.annotations.Doc;
import org.comroid.api.Polyfill;
import org.comroid.api.net.REST;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static java.time.Instant.*;
import static org.comroid.api.Polyfill.*;
import static org.comroid.api.net.REST.Method.*;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "libmod_players")
public class Player extends DbObject {
    /*
    watch me literally not give a single fuck at this point.
    i've spent years and years trying to find a good solution to instantiate a self-referencing type generic
    but they are always impossible to compile.

    so now, fuck that shit and we're abusing Polyfill.uncheckedCast().
    fuck you, java. fix your type generics.
     */
    public static final EntityType<Player, Builder<Player, ?>> TYPE
                                                                                  = Polyfill.uncheckedCast(new EntityType<>(Player::builder,
            null,
            Player.class,
            Builder.class));
    public static final Comparator<Map.Entry<?, Instant>>      MOST_RECENTLY_SEEN = Comparator.comparingLong(e -> e.getValue().toEpochMilli());
    public static       BiConsumer<UUID, String>               CACHE_NAME         = null;

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

    @Singular
    @ElementCollection
    @Column(name = "seen")
    @MapKeyColumn(name = "name")
    @CollectionTable(name = "libmod_playerdata_names", joinColumns = @JoinColumn(name = "id"))
    Map<@Doc("name") String, @Doc("seen") Instant> knownNames = new HashMap<>();
    @Singular
    @ElementCollection
    @Column(name = "seen")
    @MapKeyColumn(name = "ip")
    @CollectionTable(name = "libmod_playerdata_ips", joinColumns = @JoinColumn(name = "id"))
    Map<@Doc("ip") String, @Doc("seen") Instant>   knownIPs   = new HashMap<>();
    private String name;

    @Transient
    @JsonIgnore
    public CompletableFuture<String> getOrFetchUsername() {
        return Optional.ofNullable(name)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> fetchUsername(id));
    }

    @JsonIgnore
    public Optional<String> getLastKnownName() {
        return knownNames.entrySet().stream()
                .max(Player.MOST_RECENTLY_SEEN)
                .map(Map.Entry::getKey);
    }

    @JsonIgnore
    public Optional<String> getLastKnownIp() {
        return knownIPs.entrySet().stream()
                .max(Player.MOST_RECENTLY_SEEN)
                .map(Map.Entry::getKey);
    }

    public @Nullable String getHeadUrl() {
        return "https://mc-heads.net/avatar/" + name;
    }

    @Contract(value = "!null->this", pure = true)
    public Player pushKnownName(String name) {
        var map = getKnownNames();
        map = new HashMap<>(map);
        map.compute(name, ($0, $1) -> now());
        return this;
    }

    @Contract(value = "!null->this", pure = true)
    public Player pushKnownIp(InetAddress ip) {
        var map = getKnownIPs();
        map = new HashMap<>(map);
        map.compute(ip2string(ip), ($0, $1) -> now());
        return this;
    }
}
