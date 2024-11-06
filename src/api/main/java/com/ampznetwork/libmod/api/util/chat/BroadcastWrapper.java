package com.ampznetwork.libmod.api.util.chat;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.entity.Player;
import lombok.Value;
import org.comroid.api.data.bind.DataStructure;
import org.comroid.api.func.ext.StreamSupplier;
import org.comroid.api.func.util.Streams;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Value
public class BroadcastWrapper extends MessageWrapper {
    LibMod lib;
    String name;

    @Override
    protected Stream<Player> getTargets() {
        return lib.getPlayerAdapter().getCurrentPlayers();
    }

    @Override
    protected BroadcastWrapper wrapper() {
        return this;
    }

    public MessageWrapper target(Object... playersAndIds) {
        return new TargetAudience(() -> Stream.of(playersAndIds)
                .flatMap(BroadcastWrapper::unwrapUnknownPlayerType)
                .flatMap(id -> lib.getPlayerAdapter().getPlayer(id).stream()));
    }

    public MessageWrapper target(UUID... playerIds) {
        return new TargetAudience(() -> Stream.of(playerIds)
                .flatMap(id -> lib.getPlayerAdapter().getPlayer(id).stream()));
    }

    public MessageWrapper target(Player... players) {
        return new TargetAudience(StreamSupplier.of(players));
    }

    public MessageWrapper target(List<Player> players) {
        return new TargetAudience(players::stream);
    }

    @Value
    public class TargetAudience extends MessageWrapper {
        StreamSupplier<Player> players;

        @Override
        protected Stream<Player> getTargets() {
            return players.stream();
        }

        @Override
        protected BroadcastWrapper wrapper() {
            return BroadcastWrapper.this;
        }
    }

    private static Stream<UUID> unwrapUnknownPlayerType(Object source) {
        if (source == null)
            return Stream.empty();
        if (source instanceof UUID id)
            return Stream.of(id);
        if (source instanceof String str)
            return unwrapUnknownPlayerType(UUID.fromString(str));
        var properties = DataStructure.of(source.getClass()).getProperties();
        return Stream.of("uniqueid", "playerid", "unique_id", "player_id", "uuid", "id")
                .flatMap(testName -> properties.stream()
                        .filter(p -> Stream.of(UUID.class, String.class).anyMatch(c -> c.isAssignableFrom(p.getType().getTargetClass()))
                                     && p.getName().toLowerCase().contains(testName)))
                .sorted(Comparator.comparingInt(p -> p.getName().length() * -1))
                .map(p -> p.getFrom(source))
                .flatMap(Stream::ofNullable)
                .map(x -> x instanceof UUID id ? id : UUID.fromString(String.valueOf(x)))
                .collect(Streams.atLeastOneOrElseFlatten(() -> unwrapUnknownPlayerType(String.valueOf(source))));
    }
}
