package com.ampznetwork.libmod.api.addon;

public interface ModComponent {
    Mod getMod();

    interface Spigot extends ModComponent {
    }

    interface Fabric extends ModComponent {
    }

    interface Forge extends ModComponent {
    }
}
