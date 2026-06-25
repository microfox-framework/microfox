package ir.moke.microfox.jpa;


import ir.moke.microfox.exception.MicroFoxException;
import jakarta.persistence.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JpaFactory {
    private static final Logger logger = LoggerFactory.getLogger(JpaFactory.class);
    private static final List<EntityManagerFactory> CONNECTION_FACTORY_LIST = new ArrayList<>();
    private static final Map<String, MetadataSources> METADATA_SOURCES_MAP = new ConcurrentHashMap<>();
    private static final ScopedValue<Map<String, EntityManager>> SCOPED_VALUE = ScopedValue.newInstance();
    private static final Object validatorFactory;

    static {
        validatorFactory = getMicroFoxValidationFactory();
    }

    private JpaFactory() {
    }

    static void registerWithEntities(String identity, Set<Class<?>> entities, Map<String, Object> settings) {
        if (CONNECTION_FACTORY_LIST.stream().map(EntityManagerFactory::getName).anyMatch(item -> item.equalsIgnoreCase(identity)))
            throw new MicroFoxException("Jpa with identity %s already exists".formatted(identity));

        PersistenceConfiguration configuration = new PersistenceConfiguration(identity);
        Optional.ofNullable(validatorFactory).ifPresent(item -> settings.put(AvailableSettings.JAKARTA_VALIDATION_FACTORY, item));
        settings.forEach(configuration::property);
        entities.forEach(configuration::managedClass);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(configuration);
        CONNECTION_FACTORY_LIST.add(emf);

        // extract MetaSource
        getMetaSource(identity, entities, settings);
    }

    @SuppressWarnings("unchecked")
    static void registerWithPackage(String identity, Set<String> scanPackages, Map<String, Object> settings) {
        if (CONNECTION_FACTORY_LIST.stream().map(EntityManagerFactory::getName).anyMatch(item -> item.equalsIgnoreCase(identity)))
            throw new MicroFoxException("Jpa with identity %s already exists".formatted(identity));

        PersistenceConfiguration configuration = new PersistenceConfiguration(identity);
        Optional.ofNullable(validatorFactory).ifPresent(item -> settings.put(AvailableSettings.JAKARTA_VALIDATION_FACTORY, item));
        settings.forEach(configuration::property);
        Collection<ClassLoader> classLoaders = (Collection<ClassLoader>) settings.get(AvailableSettings.CLASSLOADERS);
        Set<Class<?>> entities = scanPackages(scanPackages, classLoaders);
        entities.forEach(configuration::managedClass);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(configuration);
        CONNECTION_FACTORY_LIST.add(emf);

        // extract MetaSource
        getMetaSource(identity, entities, settings);
    }

    static void unregister(String identity) {
        EntityManagerFactory emf = getEntityManagerFactory(identity);
        MetadataSources metadataSources = METADATA_SOURCES_MAP.remove(identity);
        ServiceRegistry serviceRegistry = metadataSources.getServiceRegistry();
        StandardServiceRegistryBuilder.destroy(serviceRegistry);
        emf.close();
        CONNECTION_FACTORY_LIST.remove(emf);
    }

    private static void getMetaSource(String identity, Set<Class<?>> entities, Map<String, Object> settings) {
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
        registryBuilder.applySettings(settings);
        MetadataSources sources = new MetadataSources(registryBuilder.build());
        sources.addAnnotatedClasses(entities.toArray(Class[]::new));
        METADATA_SOURCES_MAP.put(identity, sources);
    }

    private static Set<Class<?>> scanPackages(Set<String> packages, Collection<ClassLoader> classLoader) {
        if (packages != null && !packages.isEmpty()) {
            ConfigurationBuilder builder = new ConfigurationBuilder()
                    .forPackages(packages.toArray(String[]::new))
                    .addScanners(Scanners.TypesAnnotated);

            if (classLoader != null) builder.addClassLoaders(classLoader.toArray(ClassLoader[]::new));

            Reflections reflections = new Reflections(builder);
            return reflections.getTypesAnnotatedWith(Entity.class);
        }
        return Set.of();
    }

    static EntityManagerFactory getEntityManagerFactory(String identity) {
        return CONNECTION_FACTORY_LIST.stream().filter(item -> item.getName().equalsIgnoreCase(identity)).findFirst().orElseThrow(() -> new MicroFoxException("EntityManagerFactory with name %s not registered yet".formatted(identity)));
    }

    static ScopedValue<Map<String, EntityManager>> getScopedValue() {
        return SCOPED_VALUE;
    }

    static EntityManager getEntityManager(String identity) {
        return SCOPED_VALUE.get().get(identity);
    }

    static MetadataSources getMetadataSources(String identity) {
        return METADATA_SOURCES_MAP.get(identity);
    }

    private static Object getMicroFoxValidationFactory() {
        try {
            Class<?> microfoxValidatorClass = Class.forName("ir.moke.microfox.http.validation.MicroFoxValidator");
            Field field = microfoxValidatorClass.getDeclaredField("factory");
            return field.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            logger.warn("MicrofoxValidator not registered");
        }
        return null;
    }
}