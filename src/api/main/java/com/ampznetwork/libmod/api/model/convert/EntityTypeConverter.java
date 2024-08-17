package com.ampznetwork.libmod.api.model.convert;

import com.ampznetwork.libmod.api.model.EntityType;
import lombok.Value;

import javax.persistence.AttributeConverter;
import java.util.UUID;

@Value
public class EntityTypeConverter implements AttributeConverter<EntityType<UUID, ?, ?>, String> {
    @Override
    public String convertToDatabaseColumn(EntityType<UUID, ?, ?> attribute) {
        return attribute.getDtype();
    }

    @Override
    public EntityType<UUID, ?, ?> convertToEntityAttribute(String dbData) {
        return EntityType.REGISTRY.get(dbData);
    }
}
