package com.ampznetwork.libmod.api.model;

import com.ampznetwork.libmod.api.entity.DbObject;
import lombok.Value;
import org.comroid.api.Polyfill;
import org.comroid.api.map.Cache;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Value
public class EntityType<Entity extends DbObject, Builder> {
    public static final Map<String, EntityType<?, ?>> REGISTRY = new ConcurrentHashMap<>();

    public static <T extends DbObject> Optional<EntityType<T, ?>> find(Class<? extends T> type) {
        return REGISTRY.values().stream()
                .sorted(Comparator.<EntityType<?, ?>>comparingInt(EntityType::getImplementationDepth).reversed())
                .filter(it -> type.isAssignableFrom(it.entityType))
                .findFirst()
                .map(Polyfill::uncheckedCast);
    }

    Cache<UUID, Entity> cache;
    Supplier<Builder>   builder;
    EntityType<?, ?>    parent;
    Class<Entity>       entityType;
    Class<Builder> builderType;

    public EntityType(Supplier<? extends Builder> builder, EntityType<?, ?> parent, Class<Entity> entityType, Class<? extends Builder> builderType) {
        this.cache       = new Cache<>(DbObject::getId, (id, it) -> {}, WeakReference::new);
        this.builder     = Polyfill.uncheckedCast(builder);
        this.parent      = parent;
        this.entityType  = entityType;
        this.builderType = Polyfill.uncheckedCast(builderType);

        REGISTRY.put(entityType.getSimpleName(), this);
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
