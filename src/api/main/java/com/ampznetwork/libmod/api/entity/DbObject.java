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
import javax.persistence.Transient;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.UUID;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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
    public EntityType<?, ?> getEntityType() {
        return EntityType.REGISTRY.values().stream()
                .filter(type -> type.getEntityType().isAssignableFrom(getClass()))
                .max(Comparator.comparingInt(EntityType::getImplementationDepth))
                .orElseThrow(() -> new NoSuchElementException("Could not resolve entity type for class " + getClass()));
    }
}
