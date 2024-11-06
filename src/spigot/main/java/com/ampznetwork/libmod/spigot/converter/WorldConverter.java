package com.ampznetwork.libmod.spigot.converter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.comroid.api.func.util.Command;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
            throw new Command.Error("World with name '"+worldName+"' could not be found");
        return world;
    }
}
