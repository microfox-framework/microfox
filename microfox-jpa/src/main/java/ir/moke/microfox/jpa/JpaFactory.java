package ir.moke.microfox.jpa;


import jakarta.persistence.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JpaFactory {
    private static final Logger logger = LoggerFactory.getLogger(JpaFactory.class);
    private static final Map<String, EntityManagerFactory> CONNECTION_FACTORY_MAP = new ConcurrentHashMap<>();
    private static final Map<String, MetadataSources> METADATA_SOURCES_MAP = new ConcurrentHashMap<>();
    private static final ScopedValue<Map<String, EntityManager>> SCOPED_VALUE = ScopedValue.newInstance();
    private static final Object validatorFactory;

    static {
        validatorFactory = getMicroFoxValidationFactory();
    }

    private JpaFactory() {
    }

    public static void register(String identity, List<String> scanPackages, Map<String, Object> settings) {
        PersistenceConfiguration configuration = new PersistenceConfiguration(identity);
        Optional.ofNullable(validatorFactory).ifPresent(item -> settings.put(AvailableSettings.JAKARTA_VALIDATION_FACTORY, item));
        settings.forEach(configuration::property);
        Set<Class<?>> entities = scanPackages(scanPackages);
        entities.forEach(configuration::managedClass);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(configuration);
        CONNECTION_FACTORY_MAP.put(identity, emf);

        // extract MetaSource
        getMetaSource(identity, entities, settings);
    }

    private static void getMetaSource(String identity, Set<Class<?>> entities, Map<String, Object> settings) {
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
        registryBuilder.applySettings(settings);
        MetadataSources sources = new MetadataSources(registryBuilder.build());
        sources.addAnnotatedClasses(entities.toArray(Class[]::new));
        METADATA_SOURCES_MAP.put(identity, sources);
    }

    private static Set<Class<?>> scanPackages(List<String> packages) {
        Set<Class<?>> result = new HashSet<>();
        if (packages != null && !packages.isEmpty()) {
            for (String p : packages) {
                Reflections reflections = new Reflections(p);
                Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
                result.addAll(entityClasses);
            }
        }
        return result;
    }

    public static EntityManagerFactory getEntityManagerFactory(String identity) {
        return CONNECTION_FACTORY_MAP.get(identity);
    }

    public static ScopedValue<Map<String, EntityManager>> getScopedValue() {
        return SCOPED_VALUE;
    }

    public static EntityManager getEntityManager(String identity) {
        return SCOPED_VALUE.get().get(identity);
    }

    public static MetadataSources getMetadataSources(String identity) {
        return METADATA_SOURCES_MAP.get(identity);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> repositoryInterface, String identity) {
        return (T) Proxy.newProxyInstance(repositoryInterface.getClassLoader(), new Class<?>[]{repositoryInterface}, new RepositoryHandler(identity));
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