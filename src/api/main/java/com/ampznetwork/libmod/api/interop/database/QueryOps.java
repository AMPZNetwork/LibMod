package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import org.comroid.api.func.util.GetOrCreate;

import java.util.Optional;
import java.util.stream.Stream;

public interface QueryOps<Key, It extends DbObject, Builder extends DbObject.Builder<It, Builder>> {
    Stream<It> all();

    Optional<It> get(Key key);

    GetOrCreate<It, Builder> getOrCreate(Key key);
}
