package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import org.comroid.api.func.util.GetOrCreate;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface EntityAccessor<It extends DbObject, Builder extends DbObject.Builder<It, ?>> extends QueryOps<UUID, It, Builder> {
    IEntityService getService();

    EntityManager getManager();

    Class<It> getEntityType();

    //@Language(value = "SQL",prefix = "select x.* from libmod_player x where x.",suffix = " = :key")
    Stream<It> querySelect(Query query);

    void queryUpdate(Query query);

    default Optional<It> update(It x, Consumer<It> modify) {
        return get(x.getId()).filter(it -> {
            modify.accept(it);
            getService().save(it);
            return true;
        });
    }

    default Optional<It> update(UUID key, Consumer<It> modify) {
        return get(key).filter(it -> {
            modify.accept(it);
            getService().save(it);
            return true;
        });
    }

    default Stream<It> update(Predicate<It> filter, Consumer<It> modify) {
        return all().filter(filter)
                .peek(modify)
                .peek(getService()::save);
    }

    default GetOrCreate<It, Builder> create() {
        UUID         id;
        Optional<It> result;
        do {
            result = get(id = UUID.randomUUID());
        } while (result.isPresent());
        return getOrCreate(id).setGet(null);
    }

    default Stream<It> querySelect(@Language("SQL") String query) {return querySelect(query, null);}

    default Stream<It> querySelect(@Language("SQL") String query, @Nullable Map<String, Object> parameters) {
        //noinspection SqlSourceToSinkFlow
        var q = getManager().createNativeQuery(query, getEntityType());
        if (parameters != null)
            parameters.forEach(q::setParameter);
        return querySelect(q);
    }

    default void queryUpdate(@Language("SQL") String query, @Nullable Map<String, Object> parameters) {
        //noinspection SqlSourceToSinkFlow
        var q = getManager().createNativeQuery(query, getEntityType());
        if (parameters != null)
            parameters.forEach(q::setParameter);
        queryUpdate(q);
    }
}
