package com.ampznetwork.libmod.core.command;

import com.ampznetwork.libmod.api.LibMod;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.comroid.api.func.util.Command;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

@UtilityClass
public class LibModCommands {
    @Command
    public Component reload(LibMod mod) {
        mod.reload();
        return text("Configuration reloaded!").color(GREEN);
    }
}
