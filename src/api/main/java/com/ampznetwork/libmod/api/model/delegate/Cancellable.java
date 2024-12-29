package com.ampznetwork.libmod.api.model.delegate;

import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;

public interface Cancellable {
    boolean isCancelled();

    boolean isForced();

    void cancel();

    void force();

    @Value
    @NoArgsConstructor
    class Stateful implements Cancellable {
        @NonFinal
        boolean cancelled;
        @NonFinal
        boolean forced;

        @Override
        public void cancel() {
            cancelled = true;
            forced    = false;
        }

        @Override
        public void force() {
            cancelled = false;
            forced    = true;
        }
    }
}
