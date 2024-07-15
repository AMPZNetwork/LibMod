package com.ampznetwork.libmod.api.addon;

import com.ampznetwork.libmod.api.adapter.IEntityService;
import com.ampznetwork.libmod.api.adapter.IPlayerAdapter;
import com.ampznetwork.libmod.api.display.IDisplayAdapter;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.comroid.api.java.StackTraceUtils;

import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Set;
import java.util.function.Function;

@Value
@Builder
public class Registry {
    static {
        StackTraceUtils.EXTRA_FILTER_NAMES.add("com.ampznetwork");
    }

    String addonId;
    String addonName;
    String gitHubUrl;

    @Singular
    Set<PersistenceUnitInfo> persistenceUnits;

    Function<Mod, IEntityService> entityServiceFactory;
    Function<Mod, IPlayerAdapter> playerAdapterFactory;
    Function<Mod, IDisplayAdapter<?>> displayAdapterFactory;
}
