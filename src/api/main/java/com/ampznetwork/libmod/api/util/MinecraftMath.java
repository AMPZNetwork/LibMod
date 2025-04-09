package com.ampznetwork.libmod.api.util;

import org.comroid.api.data.Vector;

public class MinecraftMath {
    public static final Vector.N3 UNIT_CHUNK = new Vector.N3(16, 1, 16);

    public static Vector.N3 chunk2pos(Vector.N3 chunk) {
        return (Vector.N3) chunk.muli(UNIT_CHUNK);
    }

    public static Vector.N3 pos2chunk(Vector.N3 position) {
        return (Vector.N3) position.divi(UNIT_CHUNK);
    }
}
