package com.ampznetwork.libmod.api.model;

import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.Player;
import lombok.Value;
import org.comroid.annotations.Instance;
import org.comroid.api.func.util.Command;
import org.comroid.api.func.util.Streams;

import java.util.stream.Stream;

public interface AutoFillProvider {
    @Value
    class WorldNames implements Command.AutoFillProvider {
        public static final @Instance WorldNames INSTANCE = new WorldNames();

        @Override
        public Stream<String> autoFill(Command.Usage usage, String s, String s1) {
            return usage.getContext()
                    .stream()
                    .flatMap(Streams.cast(SubMod.class))
                    .flatMap(sub -> sub.getLib().worldNames());
        }
    }

    @Value
    class PlayerNames implements Command.AutoFillProvider {
        public static final @Instance WorldNames INSTANCE = new WorldNames();

        @Override
        public Stream<String> autoFill(Command.Usage usage, String s, String s1) {
            return usage.getContext()
                    .stream()
                    .flatMap(Streams.cast(SubMod.class))
                    .flatMap(sub -> sub.getPlayerAdapter().getCurrentPlayers().map(Player::getName));
        }
    }
}
