package com.ampznetwork.libmod.api.entity;

import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.convert.UuidVarchar36Converter;
import com.ampznetwork.libmod.api.util.NameGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.comroid.annotations.Ignore;
import org.comroid.api.attr.UUIDContainer;
import org.comroid.api.text.Capitalization;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DbObject implements UUIDContainer {
    @Transient
    @JsonIgnore
    protected final EntityType<?, ?> dtype = EntityType.REGISTRY.get(getClass().getSimpleName());
    @Id
    @Default
    @JdbcTypeCode(SqlTypes.UUID)
    @Convert(converter = UuidVarchar36Converter.class)
    @Column(columnDefinition = "varchar(36)", updatable = false, nullable = false)
    //@GeneratedValue(generator = "UUID") @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    protected       UUID             id    = UUID.randomUUID();

    @Ignore
    @Override
    @Transient
    @JsonIgnore
    public UUID getUuid() {
        return getId();
    }

    @Data
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
    public static abstract class WithName extends DbObject {
        private @Basic @Default String name = NameGenerator.NOUNS.apply(Capitalization.lower_snake_case);
    }

    @Data
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
    public static abstract class WithPoiName extends DbObject {
        private @Basic @Default String name = NameGenerator.POI.get();
    }
}
