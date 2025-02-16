package com.ampznetwork.libmod.spigot.adapter;

import com.ampznetwork.libmod.api.model.delegate.Cancellable;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
@RequiredArgsConstructor
public class SpigotPropagationAdapter implements Cancellable, org.bukkit.event.Cancellable {
    org.bukkit.event.Cancellable cancellable;
    @NonFinal boolean forced = false;

    @Override
    public boolean isCancelled() {
        return cancellable.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        var prev = isCancelled();
        if (prev) {
            if (!cancel) force();
        } else if (cancel) cancel();
    }

    @Override
    public void cancel() {
        cancellable.setCancelled(true);
        forced = false;
    }

    @Override
    public void force() {
        cancellable.setCancelled(false);
        forced = true;
    }

    @Override
    public String toString() {
        return cancellable.getClass().getSimpleName();
    }
}
