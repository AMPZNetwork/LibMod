package com.ampznetwork.libmod.api.entity;

import com.ampznetwork.libmod.api.interop.database.EntityType;
import com.ampznetwork.libmod.api.model.convert.UuidVarchar36Converter;
import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.UUID;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DbObject {
    protected @Id @Convert(converter = UuidVarchar36Converter.class) UUID   id;
    protected @Basic                                                 String dtype = getClass().getSimpleName();

    public EntityType<?, ?, ?> getEntityType() {
        return EntityType.REGISTRY.values().stream()
                .filter(type -> type.getEntityType().isAssignableFrom(getClass()))
                .max(Comparator.comparingInt(EntityType::getImplementationDepth))
                .orElseThrow(() -> new NoSuchElementException("Could not resolve entity type for class " + getClass()));
    }
}
