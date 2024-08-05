package com.ampznetwork.libmod.api.model;

import com.ampznetwork.libmod.api.entity.DbObject;
import lombok.Value;
import org.comroid.api.map.Cache;

import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Value
public class EntityType<Entity extends DbObject, Builder extends DbObject.Builder<Entity, Builder>> {
    public static final Map<String, EntityType<?, ?>> REGISTRY = new ConcurrentHashMap<>();

    PersistenceUnitInfo persistenceUnit;
    Cache<UUID, Entity> cache;
    Supplier<Builder>   builder;
    EntityType<?, ?>    parent;
    Class<Entity>       entityType;
    Class<Builder>      builderType;

    public String getDtype() {
        return entityType.getSimpleName();
    }

    public int getImplementationDepth() {
        var each = parent;
        var c    = 0;
        while (each != null) {
            each = each.parent;
            c += 1;
        }
        return c;
    }

    public Builder builder() {
        return builder.get();
    }
}
