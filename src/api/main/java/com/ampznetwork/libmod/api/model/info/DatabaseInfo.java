package com.ampznetwork.libmod.api.model.info;

import com.ampznetwork.libmod.api.adapter.IEntityService;

public record DatabaseInfo(
        IEntityService.DatabaseType type,
        String url,
        String user,
        String pass
) {
}
