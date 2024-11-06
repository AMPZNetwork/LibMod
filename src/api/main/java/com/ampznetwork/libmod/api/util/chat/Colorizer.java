package com.ampznetwork.libmod.api.util.chat;

import net.kyori.adventure.text.format.TextColor;

public interface Colorizer {
    TextColor getPrimaryColor();

    TextColor getSecondaryColor();

    TextColor getAccentColor();

    TextColor getDecorationColor();
}
