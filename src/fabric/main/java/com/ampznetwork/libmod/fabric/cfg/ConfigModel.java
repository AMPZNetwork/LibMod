package com.ampznetwork.libmod.fabric.cfg;

import blue.endless.jankson.Comment;
import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Modmenu(modId = LibMod.Strings.AddonId)
@SuppressWarnings({ "FieldMayBeFinal", "unused" })
@Config(name = "banMod-config", wrapperName = "Config")
public class ConfigModel {
    @Nest
    public Database         database         = new Database();
    @Nest
    public MessagingService messagingService = new MessagingService();

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Database {
        public IEntityService.DatabaseType type     = IEntityService.DatabaseType.h2;
        public String                      url      = "jdbc:h2:file:./BanMod.h2";
        public String                      username = "sa";
        public String                      password = "";
    }

    public static class MessagingService {
        @Comment("'polling-db' or 'rabbit-mq'")
        public String   type     = "polling-db";
        @Comment("use with 'type=polling-db', setting 'null' will inherit the main database") @Nest
        public Database database = new Database(null, null, null, null);
        @Comment("use with 'type=polling-db'")
        public String   interval = "2s";
        @Comment("use with 'type=rabbit-mq', example: amqp://anonymous:anonymous@localhost:5672/banmod_messaging")
        public String   uri      = null;
    }
}
