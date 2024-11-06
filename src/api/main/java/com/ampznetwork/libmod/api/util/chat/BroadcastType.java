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
    INFO(DARK_AQUA, GRAY, AQUA, DARK_GRAY),
    WARNING(DARK_AQUA, YELLOW, AQUA, DARK_GRAY),
    ERROR(DARK_AQUA, RED, AQUA, DARK_GRAY);

    TextColor primaryColor;
    TextColor secondaryColor;
    TextColor accentColor;
    TextColor decorationColor;
}
