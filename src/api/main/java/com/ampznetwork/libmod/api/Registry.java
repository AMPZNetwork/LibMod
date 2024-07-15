package com.ampznetwork.libmod.api;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.comroid.api.java.StackTraceUtils;

import javax.persistence.spi.PersistenceUnitInfo;
import java.util.Set;

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
}
