package com.ampznetwork.libmod.api.model.delegate;

import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.util.MinecraftMath;
import org.comroid.annotations.Instance;
import org.comroid.api.data.Vector;

import java.util.ServiceLoader;

public abstract class PlatformDelegate<ModPlatformBase extends SubMod> {
    @SuppressWarnings("unchecked") public static final @Instance PlatformDelegate<SubMod> INSTANCE = ServiceLoader.load(PlatformDelegate.class)
            .iterator()
            .next();

    public abstract void enableChunkloading(ModPlatformBase mod, String worldName, Vector.N2 chunk);

    public abstract void disableChunkloading(ModPlatformBase mod, String worldName, Vector.N2 chunk);

    protected Vector.N3 standardizeVector(Vector.N2 a) {
        if (a instanceof Vector.N3)
            // is position
            return (Vector.N3) a.divi(MinecraftMath.UNIT_CHUNK).intCeil();
        // is chunk id
        var z = a.getY();
        a.setY(0);
        return a.to3(z);
    }
}
