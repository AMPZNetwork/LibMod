package com.ampznetwork.libmod.api.model.info;

import com.ampznetwork.libmod.api.database.EntityService;

public record DatabaseInfo(
        EntityService.DatabaseType type,
        String url,
        String user,
        String pass
) {
}
