package com.ampznetwork.libmod.api.util.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.comroid.annotations.Doc;

import static net.kyori.adventure.text.Component.*;

public interface Colorizer {
    TextColor getTextColor();

    TextColor getAccentColor();

    TextColor getDecorationColor();

    default Component colorize(@Doc("{} styled format string") String format, Object... args) {
        var text  = text();
        var split = format.split("\\{}");
        for (int i = 0; i < split.length; i++) {
            text.append(text(split[i], getTextColor()));
            if (args.length > i)
                text.append(text(String.valueOf(args[i]), getAccentColor()));
        }
        return text.build();
    }
}
