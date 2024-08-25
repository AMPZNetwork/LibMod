package com.ampznetwork.libmod.api.model.convert;

import com.ampznetwork.libmod.api.model.EntityType;
import lombok.Value;

import javax.persistence.AttributeConverter;

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
