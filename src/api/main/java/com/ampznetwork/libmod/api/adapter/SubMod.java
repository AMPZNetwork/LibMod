package com.ampznetwork.libmod.api.adapter;

import com.ampznetwork.libmod.api.entity.DbObject;
import org.comroid.api.attr.Named;

import java.util.Collection;
import java.util.List;

public interface SubMod {
    List<Capability> getCapabilities();

    Collection<? extends DbObject> getEntityTypes();

    enum Capability implements Named {
        Database
    }
}
