package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Value
@NonFinal
public class EntityType<Key, Entity extends DbObject, Builder> {
    public static final Map<String, EntityType<?, ?, ?>> REGISTRY = new ConcurrentHashMap<>();

    @Nullable EntityType<?, ?, ?> parent;
    Class<Key>     keyType;
    Class<Entity>  entityType;
    Class<Builder> builderType;

    {
        REGISTRY.put(getDtype(), this);
    }

    public final int getImplementationDepth() {
        var each = parent;
        var c    = 0;
        while (each != null) {
            each = each.parent;
            c += 1;
        }
        return c;
    }

    public final String getDtype() {
        return entityType.getSimpleName();
    }
}
