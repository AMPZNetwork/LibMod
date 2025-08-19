package com.ampznetwork.libmod.api.model;

import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.Player;
import org.comroid.annotations.Instance;
import org.comroid.api.func.util.Command;
import org.comroid.api.func.util.Streams;

import java.util.stream.Stream;

public interface AutoFillProvider {
    enum WorldNames implements Command.AutoFillProvider.Strings {
        @Instance INSTANCE;

        @Override
        public Stream<String> strings(Command.Usage usage, String currentValue) {
            return usage.getContext()
                    .stream()
                    .flatMap(Streams.cast(SubMod.class))
                    .flatMap(sub -> sub.getLib().worldNames());
        }
    }

    enum PlayerNames implements Command.AutoFillProvider.Strings {
        @Instance INSTANCE;

        @Override
        public Stream<String> strings(Command.Usage usage, String currentValue) {
            return usage.getContext()
                    .stream()
                    .flatMap(Streams.cast(SubMod.class))
                    .flatMap(sub -> sub.getPlayerAdapter().getCurrentPlayers().map(Player::getName));
        }
    }
}
