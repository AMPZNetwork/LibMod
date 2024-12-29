package com.ampznetwork.libmod.core.adapter.internal;

import com.ampznetwork.libmod.api.model.delegate.Cancellable;
import com.ampznetwork.libmod.api.model.delegate.EventDelegate;
import lombok.Value;
import lombok.experimental.Delegate;
import org.comroid.api.func.ext.Accessor;
import org.comroid.api.func.ext.Ref;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value
public class EventDelegateImpl<T> implements EventDelegate<T> {
    @Delegate           Ref<T>      property;
    @Delegate @Nullable Cancellable cancellable;

    public <Obj> EventDelegateImpl(@NotNull Obj delegate, @NotNull Accessor<Obj, T> property) {
        this(delegate, property, null);
    }

    public <Obj> EventDelegateImpl(@NotNull Obj delegate, @NotNull Accessor<Obj, T> property, @Nullable Cancellable cancellable) {
        this(property.link(delegate), cancellable);
    }

    public EventDelegateImpl(@NotNull Ref<T> property, @Nullable Cancellable cancellable) {
        this.property    = property;
        this.cancellable = cancellable;
    }
}
