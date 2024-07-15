package com.ampznetwork.libmod.api.model.adp;

import com.ampznetwork.libmod.api.LibMod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.comroid.api.func.util.Command;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerAdapter extends Command.PermissionChecker.Adapter {
    LibMod getLibMod();

    boolean isOnline(UUID playerId);

    void kick(UUID playerId, TextComponent reason);

    void send(UUID playerId, TextComponent component);

    void broadcast(@Nullable String recieverPermission, Component component);

    void openBook(UUID playerId, BookAdapter book);
}
