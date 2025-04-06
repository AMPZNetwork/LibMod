package com.ampznetwork.libmod.fabric.config;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import jdk.jfr.Name;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
@Name(LibMod.Strings.AddonId)
public class LibModConfig extends Config {
    String serverName = "Minecraft";
    DatabaseInfo     database;
    MessagingService messagingService;

    @Data
    public static class MessagingService {
        String type;
        @Nullable DatabaseInfo database;
        @Nullable String       interval;
        @Nullable String       uri;
    }
}
