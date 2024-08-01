package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import org.comroid.api.func.util.GetOrCreate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface EntityAccessor<Key, It extends DbObject, Builder> {
    Class<Key> getKeyType();

    Class<It> getTargetType();

    Stream<It> all();

    CompletableFuture<It> fetch(Key key);

    Optional<It> get(Key key);

    GetOrCreate<It, Builder> getOrCreate(Key key);
}
