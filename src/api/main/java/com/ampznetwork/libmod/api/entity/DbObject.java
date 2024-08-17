package com.ampznetwork.libmod.api.entity;

import com.ampznetwork.libmod.api.model.EntityType;
import com.ampznetwork.libmod.api.model.convert.UuidBinary16Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
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
import javax.persistence.Transient;
import java.util.NoSuchElementException;
import java.util.UUID;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "libmod_entities")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DbObject {
    @Id
    @lombok.Builder.Default
    @GeneratedValue(generator = "UUID")
    @Convert(converter = UuidBinary16Converter.class)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "binary(16)", updatable = false, nullable = false)
    protected UUID   id    = UUID.randomUUID();
    @Basic @lombok.Builder.Default
    protected String dtype = getClass().getSimpleName();

    @Transient
    public final EntityType<?, ?> getEntityType() {
        return EntityType.find(getClass())
                .orElseThrow(() -> new NoSuchElementException("Could not resolve entity type for class " + getClass()));
    }
}
