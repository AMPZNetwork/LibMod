package com.ampznetwork.libmod.api.display;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.text.Text;

public interface DisplayAdapter<C> {
    C toDisplayable(Component component);

    String toPlaintext(Component component);

    interface Spigot extends DisplayAdapter<BaseComponent> {
    }

    interface Fabric extends DisplayAdapter<Text> {
    }

    interface Forge extends DisplayAdapter<net.minecraftforge.server.command.TextComponentHelper> {
    }
}
