package com.ampznetwork.libmod.core.adapter;

import com.ampznetwork.libmod.api.addon.Mod;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public class AbstractAdapter {
    Mod mod;
}
