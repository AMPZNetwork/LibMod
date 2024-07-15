package com.ampznetwork.libmod.api.display;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.text.Text;

public interface IDisplayAdapter<C> {
    C toDisplayable(Component component);

    String toPlaintext(Component component);

    interface Spigot extends IDisplayAdapter<BaseComponent> {
    }

    interface Fabric extends IDisplayAdapter<Text> {
    }

    interface Forge extends IDisplayAdapter<net.minecraft.network.chat.Component> {
    }
}
