package com.ampznetwork.libmod.fabric;

import com.ampznetwork.libmod.api.LibMod;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class LibMod$Fabric extends implements ModInitializer, LibMod {
    public static final Logger LOGGER = LoggerFactory.getLogger(LibMod.Strings.AddonName);
}
