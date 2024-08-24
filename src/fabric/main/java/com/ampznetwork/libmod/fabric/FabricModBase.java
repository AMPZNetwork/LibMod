package com.ampznetwork.libmod.fabric;

import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import org.comroid.api.func.util.Command;
import org.comroid.api.func.util.Command$Manager$Adapter$Fabric;

public abstract class FabricModBase implements ModInitializer {
    @Getter
    protected     Command.Manager                cmdr = new Command.Manager();
    private final Command$Manager$Adapter$Fabric adp  = new Command$Manager$Adapter$Fabric(cmdr);

    @Override
    public void onInitialize() {
        adp.initialize();
    }
}
