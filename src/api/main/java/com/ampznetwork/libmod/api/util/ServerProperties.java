package com.ampznetwork.libmod.api.util;

import lombok.Value;
import org.comroid.api.attr.IntegerAttribute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

@Value
public class ServerProperties {
    public static final ServerProperties LOCAL;

    static {
        try {
            LOCAL = new ServerProperties(new File("./server.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load local server.properties", e);
        }
    }

    boolean               acceptsTransfers;
    boolean               allowFlight;
    boolean               allowNether;
    boolean               broadcastConsoleToOps;
    boolean               broadcastRconToOps;
    String                bugReportLink;
    Difficulty            difficulty;
    boolean               enableCommandBlock;
    boolean               enableJmxMonitoring;
    boolean               enableQuery;
    boolean               enableRcon;
    boolean               enableStatus;
    boolean               enforceSecureProfile;
    boolean               enforceWhitelist;
    byte                  entityBroadcastRangePercentage;
    boolean               forceGamemode;
    byte                  functionPermissionLevel;
    Gamemode              gamemode;
    boolean               generateStructures;
    /** todo */
    String                generatorSettings;
    boolean               hardcore;
    boolean               hideOnlinePlayers;
    /** todo */
    String                initialDisabledPacks;
    /** todo */
    String                initialEnabledPacks;
    String                levelName;
    String                levelSeed;
    String                levelType;
    int                   maxChainedNeighborUpdates;
    int                   maxPlayers;
    long                  maxTickTime;
    int                   maxWorldSize;
    String                motd;
    int                   networkCompressionThreshold;
    boolean               onlineMode;
    byte                  opPermissionLevel;
    int                   pauseWhenEmptySeconds;
    int                   playerIdleTimeout;
    boolean               preventProxyConnections;
    boolean               pvp;
    byte                  queryPort;
    int                   rateLimit;
    String                rconPassword;
    byte                  rconPort;
    RegionFileCompression regionFileCompression;
    boolean               requireResourcePack;
    UUID                  resourcePackId;
    String                resourcePackPrompt;
    String                resourcePackSha1;
    String                serverIp;
    byte                  serverPort;
    int                   simulationDistance;
    boolean               spawnMonsters;
    short                 spawnProtection;
    boolean               syncChunkWrites;
    String                textFilteringConfig;
    String                textFilteringVersion;
    boolean               useNativeTransport;
    int                   viewDistance;
    boolean               whiteList;

    public ServerProperties(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public ServerProperties(InputStream source) throws IOException {
        var props = new Properties();
        try (var src = source) {
            props.load(src);
        }

        acceptsTransfers               = Boolean.parseBoolean(props.getProperty("accepts-transfers"));
        allowFlight                    = Boolean.parseBoolean(props.getProperty("allow-flight"));
        allowNether                    = Boolean.parseBoolean(props.getProperty("allow-nether"));
        broadcastConsoleToOps          = Boolean.parseBoolean(props.getProperty("broadcast-console-to-ops"));
        broadcastRconToOps             = Boolean.parseBoolean(props.getProperty("broadcast-rcon-to-ops"));
        bugReportLink                  = props.getProperty("bug-report-link");
        difficulty                     = Difficulty.valueOf(props.getProperty("difficulty"));
        enableCommandBlock             = Boolean.parseBoolean(props.getProperty("enable-command-block"));
        enableJmxMonitoring            = Boolean.parseBoolean(props.getProperty("enable-jmx-monitoring"));
        enableQuery                    = Boolean.parseBoolean(props.getProperty("enable-query"));
        enableRcon                     = Boolean.parseBoolean(props.getProperty("enable-rcon"));
        enableStatus                   = Boolean.parseBoolean(props.getProperty("enable-status"));
        enforceSecureProfile           = Boolean.parseBoolean(props.getProperty("enforce-secure-profile"));
        enforceWhitelist               = Boolean.parseBoolean(props.getProperty("enforce-whitelist"));
        entityBroadcastRangePercentage = Byte.parseByte(props.getProperty("entity-broadcast-range-percentage"));
        forceGamemode                  = Boolean.parseBoolean(props.getProperty("force-gamemode"));
        functionPermissionLevel        = Byte.parseByte(props.getProperty("function-permission-level"));
        gamemode                       = Gamemode.valueOf(props.getProperty("gamemode"));
        generateStructures             = Boolean.parseBoolean(props.getProperty("generate-structures"));
        generatorSettings              = props.getProperty("generator-settings");
        hardcore                       = Boolean.parseBoolean(props.getProperty("hardcore"));
        hideOnlinePlayers              = Boolean.parseBoolean(props.getProperty("hide-online-players"));
        initialDisabledPacks           = props.getProperty("initial-disabled-packs");
        initialEnabledPacks            = props.getProperty("initial-enabled-packs");
        levelName                      = props.getProperty("level-name");
        levelSeed                      = props.getProperty("level-seed");
        levelType                      = props.getProperty("level-type");
        maxChainedNeighborUpdates      = Integer.parseInt(props.getProperty("max-chained-neighbor-updates"));
        maxPlayers                     = Integer.parseInt(props.getProperty("max-players"));
        maxTickTime                    = Long.parseLong(props.getProperty("max-tick-time"));
        maxWorldSize                   = Integer.parseInt(props.getProperty("max-world-size"));
        motd                           = props.getProperty("motd");
        networkCompressionThreshold    = Integer.parseInt(props.getProperty("network-compression-threshold"));
        onlineMode                     = Boolean.parseBoolean(props.getProperty("online-mode"));
        opPermissionLevel              = Byte.parseByte(props.getProperty("op-permission-level"));
        pauseWhenEmptySeconds          = Integer.parseInt(props.getProperty("pause-when-empty-seconds"));
        playerIdleTimeout              = Integer.parseInt(props.getProperty("player-idle-timeout"));
        preventProxyConnections        = Boolean.parseBoolean(props.getProperty("prevent-proxy-connections"));
        pvp                            = Boolean.parseBoolean(props.getProperty("pvp"));
        queryPort                      = Byte.parseByte(props.getProperty("query-port"));
        rateLimit                      = Integer.parseInt(props.getProperty("rate-limit"));
        rconPassword                   = props.getProperty("rcon-password");
        rconPort                       = Byte.parseByte(props.getProperty("rcon-port"));
        regionFileCompression          = RegionFileCompression.valueOf(props.getProperty("region-file-compression"));
        requireResourcePack            = Boolean.parseBoolean(props.getProperty("require-resource-pack"));
        resourcePackId                 = UUID.fromString(props.getProperty("resource-pack-id"));
        resourcePackPrompt             = props.getProperty("resource-pack-prompt");
        resourcePackSha1               = props.getProperty("resource-pack-sha1");
        serverIp                       = props.getProperty("server-ip");
        serverPort                     = Byte.parseByte(props.getProperty("server-port"));
        simulationDistance             = Integer.parseInt(props.getProperty("simulation-distance"));
        spawnMonsters                  = Boolean.parseBoolean(props.getProperty("spawn-monsters"));
        spawnProtection                = Short.parseShort(props.getProperty("spawn-protection"));
        syncChunkWrites                = Boolean.parseBoolean(props.getProperty("sync-chunk-writes"));
        textFilteringConfig            = props.getProperty("text-filtering-config");
        textFilteringVersion           = props.getProperty("text-filtering-version");
        useNativeTransport             = Boolean.parseBoolean(props.getProperty("use-native-transport"));
        viewDistance                   = Integer.parseInt(props.getProperty("view-distance"));
        whiteList                      = Boolean.parseBoolean(props.getProperty("white-list"));
    }

    public enum Gamemode implements IntegerAttribute {
        survival, creative, adventure, spectator
    }

    public enum Difficulty implements IntegerAttribute {
        peaceful, easy, normal, hard
    }

    public enum RegionFileCompression {
        none, deflate, lz4
    }
}
