package com.ampznetwork.libmod.spigot;

import com.ampznetwork.libmod.api.AddonApi;
import com.ampznetwork.libmod.api.database.EntityService;
import com.ampznetwork.libmod.api.display.DisplayAdapter;
import com.ampznetwork.libmod.api.model.adp.PlayerAdapter;
import lombok.Value;
import net.md_5.bungee.api.chat.BaseComponent;

@Value
public class SpigotAddonApi implements AddonApi<BaseComponent> {
    EntityService entityService;
    PlayerAdapter playerAdapter;
    DisplayAdapter.Spigot displayAdapter;
}
