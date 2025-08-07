package com.ampznetwork.libmod.api.model.info;

import com.ampznetwork.libmod.api.interop.database.IEntityService;

public record DatabaseInfo(
        IEntityService.DatabaseType type,
        String url,
        String username,
        String password
) {
}
