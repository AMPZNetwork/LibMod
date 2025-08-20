package com.ampznetwork.libmod.api.model;

import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.Player;
import org.comroid.annotations.Instance;
import org.comroid.api.func.util.Streams;
import org.comroid.commands.autofill.impl.NamedAutoFillAdapter;
import org.comroid.commands.autofill.model.StringBasedAutoFillProvider;
import org.comroid.commands.impl.CommandUsage;

import java.util.stream.Stream;

public interface AutoFillProvider {
    enum WorldNames implements StringBasedAutoFillProvider {
        @Instance INSTANCE;

        @Override
        public Stream<String> strings(CommandUsage usage, String currentValue) {
            return usage.getContext()
                    .stream()
                    .flatMap(Streams.cast(SubMod.class))
                    .flatMap(sub -> sub.getLib().worldNames());
        }
    }

    enum PlayerNames implements NamedAutoFillAdapter<Player> {
        @Instance INSTANCE;

        @Override
        public Stream<Player> objects(CommandUsage usage, String currentValue) {
            return usage.getContext()
                    .stream()
                    .flatMap(Streams.cast(SubMod.class))
                    .flatMap(sub -> sub.getPlayerAdapter().getCurrentPlayers());
        }
    }
}
