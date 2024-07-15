package com.ampznetwork.libmod.core.command;

import com.ampznetwork.libmod.api.LibMod;
import lombok.experimental.UtilityClass;
import org.comroid.annotations.Instance;
import org.comroid.api.func.util.Command;

import java.util.stream.Stream;

import static java.util.stream.Stream.empty;
import static org.comroid.api.func.util.Streams.cast;

@UtilityClass
public class AutoFillProvider {
    enum Players implements Command.AutoFillProvider {
        @Instance INSTANCE;

        @Override
        public Stream<String> autoFill(Command.Usage usage, String argName, String currentValue) {
            var mod = usage.getContext().stream()
                    .flatMap(cast(LibMod.class))
                    .findAny().orElseThrow();
            return mod.getLib().getPlayerAdapter().getCurrentPlayers()
                    .flatMap(data -> data.getLastKnownName().stream())
                    .distinct();
        }
    }

    enum ObjectProperties implements Command.AutoFillProvider {
        @Instance INSTANCE;

        @Override
        public Stream<String> autoFill(Command.Usage usage, String argName, String currentValue) {
            return empty();
        }
    }

    enum ObjectPropertyValues implements Command.AutoFillProvider {
        @Instance INSTANCE;

        @Override
        public Stream<String> autoFill(Command.Usage usage, String argName, String currentValue) {
            return empty();
        }
    }
}
