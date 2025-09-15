package com.ampznetwork.libmod.core.database.hibernate;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.sql.DataSource;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@Value
@NonFinal
public class PersistenceUnitBase implements PersistenceUnitInfo {
    Class<?>     module;
    DataSource   dataSource;
    List<String> classes;

    public PersistenceUnitBase(Class<?> module, DataSource dataSource, @SuppressWarnings("rawtypes") Class... classes) {
        this(module.getSimpleName(), module, dataSource, classes);
    }

    public PersistenceUnitBase(String name, Class<?> module, DataSource dataSource, @SuppressWarnings("rawtypes") Class... classes) {
        this.module     = module;
        this.dataSource = dataSource;
        this.classes    = Stream.of(classes).map(Class::getCanonicalName).toList();
    }

    @Override
    public String getPersistenceUnitName() {
        return module.getSimpleName();
    }

    @Override
    public String getPersistenceProviderClassName() {
        return HibernatePersistenceProvider.class.getCanonicalName();
    }

    @Override
    public String getScopeAnnotationName() {
        return "";
    }

    @Override
    public List<String> getQualifierAnnotationNames() {
        return List.of();
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
        return module.getProtectionDomain().getCodeSource().getLocation();
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
        return module.getClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        // wtf?
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return getClassLoader();
    }
}
