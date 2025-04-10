package com.ampznetwork.libmod.api.util.chat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.format.TextColor;

import static net.kyori.adventure.text.format.NamedTextColor.*;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BroadcastType implements Colorizer {
    INFO(GRAY, AQUA, DARK_GRAY),
    HINT(GRAY, AQUA, DARK_GRAY),
    WARNING(YELLOW, AQUA, DARK_GRAY),
    ERROR(RED, AQUA, DARK_GRAY),
    FATAL(DARK_RED, RED, DARK_GRAY);

    TextColor textColor;
    TextColor accentColor;
    TextColor decorationColor;
}
