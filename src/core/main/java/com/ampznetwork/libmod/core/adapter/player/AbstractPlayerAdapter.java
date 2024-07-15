package com.ampznetwork.libmod.core.adapter.player;

import com.ampznetwork.libmod.api.adapter.IPlayerAdapter;
import com.ampznetwork.libmod.core.adapter.AbstractAdapter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public abstract class AbstractPlayerAdapter extends AbstractAdapter implements IPlayerAdapter {
}
