package com.ampznetwork.libmod.api.entity;

import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.convert.UuidVarchar36Converter;
import com.ampznetwork.libmod.api.util.NameGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.comroid.annotations.Default;
import org.comroid.api.text.Capitalization;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DbObject {
    @Id @lombok.Builder.Default @Convert(converter = UuidVarchar36Converter.class) @Type(type = "uuid-char")
    //@GeneratedValue(generator = "UUID") @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "varchar(36)", updatable = false, nullable = false)
    protected UUID             id    = UUID.randomUUID();
    @Transient
    protected final EntityType<?, ?> dtype = EntityType.REGISTRY.get(getClass().getSimpleName());

    @Data
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
    public static abstract class WithName extends DbObject {
        private @Basic @Default String name = NameGenerator.NOUNS.apply(Capitalization.lower_snake_case);
    }

    @Data
    @Entity
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
    public static abstract class WithPoiName extends DbObject {
        private @Basic @Default String name = NameGenerator.POI.get();
    }
}
