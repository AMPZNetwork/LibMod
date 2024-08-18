package com.ampznetwork.libmod.fabric;

import com.ampznetwork.libmod.api.LibMod;
import com.ampznetwork.libmod.api.SubMod;
import com.ampznetwork.libmod.api.entity.DbObject;
import com.ampznetwork.libmod.api.interop.database.IEntityService;
import com.ampznetwork.libmod.core.database.hibernate.HibernateEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.Set;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class SubMod$Fabric extends FabricModBase implements SubMod {
    Set<Capability>                capabilities;
    Set<Class<? extends DbObject>> entityTypes;
    @NonFinal           HibernateEntityService entityService;
    @NonFinal protected LibMod                 lib;

    @Override
    public IEntityService getEntityService() {
        if (!hasCapability(Capability.Database))
            throw new UnsupportedOperationException();
        return entityService;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        this.entityService = new HibernateEntityService(lib, this);
    }
}
