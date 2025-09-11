package com.ampznetwork.libmod.api.model.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;
import org.comroid.api.data.Vector;

@jakarta.persistence.Converter(autoApply = true)
public class VectorConverter implements AttributeConverter<Vector.N3, String> {
    @Override
    public String convertToDatabaseColumn(Vector.N3 attribute) {
        var node = new ObjectMapper().createObjectNode();
        node.put("x", (int)attribute.getX());
        node.put("y", (int)attribute.getY());
        node.put("z", (int)attribute.getZ());
        return node.toString();
    }

    @Override
    @SneakyThrows
    public Vector.N3 convertToEntityAttribute(String dbData) {
        var node = new ObjectMapper().readTree(dbData);
        return new Vector.N3(node.get("x").asInt(), node.get("y").asInt(), node.get("z").asInt());
    }
}
