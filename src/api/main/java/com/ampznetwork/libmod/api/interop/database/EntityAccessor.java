package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import org.comroid.api.func.util.GetOrCreate;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface EntityAccessor<It extends DbObject, Builder extends DbObject.Builder<It, Builder>> {
    Class<It> getEntityType();

    Stream<It> all();

    Optional<It> get(UUID key);

    GetOrCreate<It, Builder> getOrCreate(UUID key);
}