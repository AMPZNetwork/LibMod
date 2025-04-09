package com.ampznetwork.libmod.spigot.adapter;

import com.ampznetwork.libmod.api.model.delegate.PlatformDelegate;
import com.ampznetwork.libmod.api.util.MinecraftMath;
import com.ampznetwork.libmod.spigot.SubMod$Spigot;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.comroid.api.data.Vector;
import org.comroid.api.info.Constraint;

public class SpigotPlatformDelegate extends PlatformDelegate<SubMod$Spigot> {
    @Override
    public void enableChunkloading(SubMod$Spigot mod, String worldName, Vector.N2 chunk) {
        if (!chunk(worldName, standardizeVector(chunk)).addPluginChunkTicket(mod))
            mod.getLogger().warning("Could not enable chunkloading for chunk " + chunk);
    }

    @Override
    public void disableChunkloading(SubMod$Spigot mod, String worldName, Vector.N2 chunk) {
        if (!chunk(worldName, standardizeVector(chunk)).removePluginChunkTicket(mod))
            mod.getLogger().warning("Could not disable chunkloading for chunk " + chunk);
    }

    private Chunk chunk(String worldName, Vector.N3 chunk) {
        var pos   = MinecraftMath.chunk2pos(chunk);
        var world = Bukkit.getWorld(worldName);
        Constraint.notNull(world, "world").run();
        return world.getChunkAt((int) pos.getX(), (int) pos.getZ());
    }
}
