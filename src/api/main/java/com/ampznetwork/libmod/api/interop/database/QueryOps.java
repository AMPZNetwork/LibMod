package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import org.comroid.api.func.util.GetOrCreate;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface QueryOps<Key, It extends DbObject, Builder extends DbObject.Builder<It, ?>> {
    Stream<It> all();

    default Optional<It> any() {
        return all().findAny();
    }

    Optional<It> get(Key key);

    GetOrCreate<It, Builder> getOrCreate(@Nullable Key key);

    default GetOrCreate<It, Builder> create() {
        return getOrCreate(null);
    }

    default <NewKey> QueryOps<NewKey, It, Builder> by(final Function<It, NewKey> keyFunction) {
        final var parent = this;
        return new QueryOps<>() {
            @Override
            public Stream<It> all() {
                return parent.all();
            }

            @Override
            public Optional<It> get(NewKey key) {
                return all().filter(it -> Objects.equals(keyFunction.apply(it), key)).findFirst();
            }

            @Override
            public GetOrCreate<It, Builder> getOrCreate(NewKey key) {
                return parent.getOrCreate(null).setGet(() -> get(key).orElse(null));
            }
        };
    }
}
