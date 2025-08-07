package com.ampznetwork.libmod.api.model.config;

import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import org.jetbrains.annotations.Nullable;

public interface DatabaseConfigAdapter {
    @Nullable DatabaseInfo getDatabaseInfo();
}
