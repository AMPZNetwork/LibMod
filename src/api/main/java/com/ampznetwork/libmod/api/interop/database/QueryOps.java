package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import org.comroid.api.func.util.GetOrCreate;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface QueryOps<Key, It extends DbObject, Builder extends DbObject.Builder<It, Builder>> {
    Stream<It> all();

    Optional<It> get(Key key);

    GetOrCreate<It, Builder> getOrCreate(Key key);

    default <NewKey> QueryOps<NewKey, It, Builder> by(final Function<It, NewKey> keyFunction) {
        final var parent = this;
        return new QueryOps<>() {
            @Override
            public Stream<It> all() {
                return parent.all();
            }

            @Override
            public Optional<It> get(NewKey key) {
                return all().filter(it -> keyFunction.apply(it).equals(key)).findFirst();
            }

            @Override
            public GetOrCreate<It, Builder> getOrCreate(NewKey key) {
                return parent.getOrCreate(null)
                        .setGet(() -> get(key).orElse(null));
            }
        };
    }
}