package com.ampznetwork.libmod.spigot.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.comroid.commands.model.CommandError;

@Converter(autoApply = true)
public class WorldConverter implements AttributeConverter<World,String> {
    @Override
    public String convertToDatabaseColumn(World world) {
        return world.getName();
    }

    @Override
    public World convertToEntityAttribute(String worldName) {
        var world = Bukkit.getWorld(worldName);
        if (world==null)
            throw new CommandError("World with name '" + worldName + "' could not be found");
        return world;
    }
}
