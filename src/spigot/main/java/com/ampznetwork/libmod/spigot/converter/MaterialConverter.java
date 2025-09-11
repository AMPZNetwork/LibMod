package com.ampznetwork.libmod.spigot.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bukkit.Material;

import java.util.Arrays;

@Converter(autoApply = true)
public class MaterialConverter implements AttributeConverter<Material, String> {
    @Override
    public String convertToDatabaseColumn(Material material) {
        return material.getKey().toString();
    }

    @Override
    public Material convertToEntityAttribute(String key) {
        return Arrays.stream(Material.values())
                .filter(mat -> mat.getKey().toString().equals(key))
                .findAny().orElseThrow();
    }
}
