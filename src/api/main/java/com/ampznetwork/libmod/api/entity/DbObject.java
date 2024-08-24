package com.ampznetwork.libmod.api.entity;

import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.convert.UuidVarchar36Converter;
import com.ampznetwork.libmod.api.util.NameGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.comroid.api.Polyfill;
import org.comroid.api.text.Capitalization;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "libmod_entities")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DbObject<K> {
    @Id
    @lombok.Builder.Default
    @GeneratedValue(generator = "UUID")
    @Convert(converter = UuidVarchar36Converter.class)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "varchar(255)", updatable = false, nullable = false)
    protected K id = randomId(getClass());

    @Basic @lombok.Builder.Default
    protected String dtype = getClass().getSimpleName();

    public EntityType<K, ? extends DbObject<K>, ?> getEntityType() {
        return Polyfill.uncheckedCast(EntityType.REGISTRY.get(dtype));
    }

    private static <K> K randomId(Class<?> type) {
        if (ByUuid.class.isAssignableFrom(type)) return Polyfill.uncheckedCast(ByUuid.randomId());
        if (ByName.class.isAssignableFrom(type)) return Polyfill.uncheckedCast(ByName.randomId());
        if (ByPoiName.class.isAssignableFrom(type)) return Polyfill.uncheckedCast(ByPoiName.randomId());
        throw new UnsupportedOperationException();
    }

    @Data
    @Entity
    @SuperBuilder
    @AllArgsConstructor
    public static abstract class ByUuid extends DbObject<UUID> {
        protected static UUID randomId() {
            return UUID.randomUUID();
        }
    }

    @Data
    @Entity
    @SuperBuilder
    @AllArgsConstructor
    public static abstract class ByName extends DbObject<String> {
        protected static String randomId() {
            return NameGenerator.NOUNS.apply(Capitalization.lower_snake_case);
        }
    }

    @Data
    @Entity
    @SuperBuilder
    @AllArgsConstructor
    public static abstract class ByPoiName extends DbObject<String> {
        protected static String randomId() {
            return NameGenerator.POI.get();
        }
    }
}
