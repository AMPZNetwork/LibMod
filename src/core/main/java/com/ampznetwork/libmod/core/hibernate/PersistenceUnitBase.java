package com.ampznetwork.libmod.core.hibernate;

import com.ampznetwork.libmod.api.LibMod;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import java.net.URL;
import java.util.List;
import java.util.Properties;

@Value
@NonFinal
public abstract class PersistenceUnitBase implements PersistenceUnitInfo {
    String persistenceUnitName;
    HikariDataSource dataSource;
    URL jarUrl;
    List<String> classes;

    @Override
    public String getPersistenceProviderClassName() {
        return HibernatePersistenceProvider.class.getCanonicalName();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL;
    }

    @Override
    public DataSource getJtaDataSource() {
        return dataSource;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return dataSource;
    }

    @Override
    public List<String> getMappingFileNames() {
        return List.of();
    }

    @Override
    public List<URL> getJarFileUrls() {
        return List.of();
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return jarUrl;
    }

    @Override
    public List<String> getManagedClassNames() {
        return classes;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return true;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return SharedCacheMode.ALL;
    }

    @Override
    public ValidationMode getValidationMode() {
        return ValidationMode.AUTO;
    }

    @Override
    public Properties getProperties() {
        return new Properties();
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return "1";
    }

    @Override
    public ClassLoader getClassLoader() {
        return LibMod.class.getClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        // wtf?
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return LibMod.class.getClassLoader();
    }
}
