package com.ampznetwork.libmod.api.messaging;

import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.convert.EntityTypeConverter;
import com.ampznetwork.libmod.api.model.convert.UuidBinary16Converter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.comroid.api.attr.Named;
import org.comroid.api.data.seri.DataNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Data
@Entity
@Builder
@ApiStatus.Internal
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "banmod_notify")
@IdClass(NotifyEvent.CompositeKey.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = { "ident", "timestamp" })
@ToString(of = { "type", "timestamp", "relatedId", "relatedType" })
public final class NotifyEvent extends DbObject implements DataNode {
    @Id @Column(columnDefinition = "bigint")          BigInteger       ident;
    @Id @lombok.Builder.Default                       Instant          timestamp   = Instant.now();
    @lombok.Builder.Default                           Type             type        = Type.SYNC;
    @lombok.Builder.Default @Nullable
    @Convert(converter = UuidBinary16Converter.class) UUID             relatedId   = null;
    @lombok.Builder.Default @Nullable
    @Convert(converter = EntityTypeConverter.class)   EntityType<?, ?> relatedType = null;
    @lombok.Builder.Default
    @Column(columnDefinition = "bigint")              BigInteger       acknowledge = BigInteger.valueOf(0);

    @Getter
    public enum Type implements Named, Predicate<EntityType<?, ?>> {
        /**
         * sent immediately after connecting for the first time, together with an {@code ident} value
         */
        HELLO,
        /**
         * sent with an infraction ID as {@code data} after storing an infraction
         * after polling SYNC, it is expected to merge thyself into ident
         */
        SYNC(EntityType.REGISTRY.values());

        @SuppressWarnings("rawtypes") private final Set<EntityType> allowedTypes;

        Type(Collection<EntityType<?, ?>> allowedTypes) {this(allowedTypes.toArray(new EntityType[0]));}

        Type(@SuppressWarnings("rawtypes") EntityType... allowedTypes) {
            this.allowedTypes = Set.of(allowedTypes);
        }

        @Override
        public boolean test(EntityType<?, ?> entityType) {
            return allowedTypes.contains(entityType);
        }
    }

    @Data
    public static class CompositeKey implements Serializable {
        BigInteger ident;
        Instant    timestamp;
    }
}