package com.ampznetwork.libmod.api.model;

import com.ampznetwork.libmod.api.entity.DbObject;
import lombok.Value;
import org.comroid.api.Polyfill;
import org.comroid.api.map.Cache;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Value
public class EntityType<ID, Entity extends DbObject<ID>, Builder> {
    public static final Map<String, EntityType<?, ?, ?>> REGISTRY = new ConcurrentHashMap<>();

    public static <ID, T extends DbObject<ID>> Optional<EntityType<ID, T, ?>> find(Class<? extends T> type) {
        return REGISTRY.values().stream()
                .sorted(Comparator.<EntityType<?, ?, ?>>comparingInt(EntityType::getImplementationDepth).reversed())
                .filter(it -> type.isAssignableFrom(it.entityType))
                .findFirst()
                .map(Polyfill::uncheckedCast);
    }

    Cache<ID, Entity>    cache;
    Supplier<Builder>    builder;
    EntityType<ID, ?, ?> parent;
    Class<Entity>        entityType;
    Class<Builder> builderType;

    public EntityType(Supplier<Builder> builder, EntityType<ID, ?, ?> parent, Class<Entity> entityType, Class<Builder> builderType) {
        this.cache       = new Cache<>(DbObject::getId, (id, it) -> {}, WeakReference::new);
        this.builder     = builder;
        this.parent      = parent;
        this.entityType  = entityType;
        this.builderType = builderType;
    }

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
