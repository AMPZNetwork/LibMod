package com.ampznetwork.libmod.api.interop.database;

import com.ampznetwork.libmod.api.entity.DbObject;
import org.comroid.api.func.util.GetOrCreate;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface EntityAccessor<Key, It extends DbObject<Key>, Builder extends DbObject.Builder<Key, It, Builder>> {
    EntityManager getManager();

    IEntityService getService();

    Class<It> getEntityType();

    Stream<It> all();

    default Stream<It> querySelect(@Language("SQL") String query) {return querySelect(query, null);}

    default Stream<It> querySelect(@Language("SQL") String query, @Nullable Map<String, Object> parameters) {
        //noinspection SqlSourceToSinkFlow
        var q = getManager().createNativeQuery(query, getEntityType());
        if (parameters != null)
            parameters.forEach(q::setParameter);
        return querySelect(q);
    }

    Stream<It> querySelect(Query query);

    Optional<It> get(Key key);

    default GetOrCreate<It, Builder> create() {
        Key          id;
        Optional<It> result;
        do {
            result = get(id = DbObject.randomId(getEntityType()));
        } while (result.isPresent());
        return getOrCreate(id).setGet(null);
    }

    GetOrCreate<It, Builder> getOrCreate(Key key);

    default Optional<It> update(Key key, Consumer<It> modify) {
        return get(key).filter(it -> {
            modify.accept(it);
            getService().save(it);
            return true;
        });
    }

    void queryUpdate(Query query);
}
