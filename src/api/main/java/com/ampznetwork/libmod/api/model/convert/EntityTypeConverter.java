package com.ampznetwork.libmod.api.model.convert;

import com.ampznetwork.libmod.api.model.EntityType;
import jakarta.persistence.AttributeConverter;
import lombok.Value;

@Value
public class EntityTypeConverter implements AttributeConverter<EntityType<?, ?>, String> {
    @Override
    public String convertToDatabaseColumn(EntityType<?, ?> attribute) {
        return attribute.getDtype();
    }

    @Override
    public EntityType<?, ?> convertToEntityAttribute(String dbData) {
        return EntityType.REGISTRY.get(dbData);
    }
}
