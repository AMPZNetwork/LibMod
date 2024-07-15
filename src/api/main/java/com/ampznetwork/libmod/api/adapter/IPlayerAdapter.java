package com.ampznetwork.libmod.api.adapter;

import com.ampznetwork.libmod.api.addon.Mod;
import com.ampznetwork.libmod.api.addon.ModComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.comroid.api.func.util.Command;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IPlayerAdapter extends ModComponent, Command.PermissionChecker.Adapter {
    Mod getMod();

    boolean isOnline(UUID playerId);

    void kick(UUID playerId, TextComponent reason);

    void send(UUID playerId, TextComponent component);

    void broadcast(@Nullable String recieverPermission, Component component);

    void openBook(UUID playerId, IBookAdapter book);
}
