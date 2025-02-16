package com.ampznetwork.libmod.core.adapter.internal;

import com.ampznetwork.libmod.api.model.delegate.Cancellable;
import lombok.Value;
import org.comroid.api.func.ext.Accessor;
import org.comroid.api.func.ext.Ref;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value
public class DelegatingCancellable<Prop> implements Cancellable {
    Ref<Prop> property;
    @Nullable Prop cancelledWhenValue;
    @Nullable Prop forcedWhenValue;

    public <Obj> DelegatingCancellable(
            Obj delegate,
            @NotNull Accessor<Obj, Prop> property
    ) {
        this(delegate, property, null);
    }

    public <Obj> DelegatingCancellable(
            Obj delegate,
            @NotNull Accessor<Obj, Prop> property,
            @Nullable Prop cancelledWhenValue
    ) {
        this(delegate, property, cancelledWhenValue, null);
    }

    public <Obj> DelegatingCancellable(
            Obj delegate,
            @NotNull Accessor<Obj, Prop> property,
            @Nullable Prop cancelledWhenValue,
            @Nullable Prop forcedWhenValue
    ) {
        this(property.link(delegate), cancelledWhenValue, forcedWhenValue);
    }

    public DelegatingCancellable(
            @NotNull Ref<Prop> property,
            @Nullable Prop cancelledWhenValue,
            @Nullable Prop forcedWhenValue
    ) {
        this.property           = property;
        this.cancelledWhenValue = cancelledWhenValue;
        this.forcedWhenValue    = forcedWhenValue;
    }

    @Override
    public boolean isCancelled() {
        return property.contentEquals(cancelledWhenValue);
    }

    @Override
    public boolean isForced() {
        return property.contentEquals(forcedWhenValue);
    }

    @Override
    public void cancel() {
        property.accept(cancelledWhenValue);
    }

    @Override
    public void force() {
        property.accept(forcedWhenValue);
    }

    @Override
    public String toString() {
        return property.<Accessor.Linked<?, ?>>cast().getDelegate().getClass().getSimpleName();
    }
}
