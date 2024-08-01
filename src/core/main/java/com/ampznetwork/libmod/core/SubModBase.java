package com.ampznetwork.libmod.core;

import com.ampznetwork.libmod.api.adapter.SubMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.util.List;
import java.util.Set;

@Value
@NonFinal
public class SubModBase implements SubMod {
    List<Capability>               capabilities;
    Set<Class<? extends DbObject>> entityTypes;
}
