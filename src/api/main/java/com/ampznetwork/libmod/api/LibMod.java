package com.ampznetwork.libmod.api;

import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import lombok.experimental.UtilityClass;

public interface LibMod extends Mod, AddonApi {
    DatabaseInfo getDatabaseInfo();

    @UtilityClass
    final class Strings {
        public static final String AddonId = "libmod";
        public static final String AddonName = "LibMod";
        public static final String PleaseCheckConsole = "Please check console for further information";
    }
}
