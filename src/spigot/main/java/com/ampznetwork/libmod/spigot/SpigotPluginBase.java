package com.ampznetwork.libmod.spigot;

import com.ampznetwork.libmod.api.AddonApi;
import com.ampznetwork.libmod.api.database.EntityService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public abstract class SpigotPluginBase extends JavaPlugin implements AddonApi.Spigot {
    private final SpigotAddonApi api = new SpigotAddonApi();
    private EntityService entityService;

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
