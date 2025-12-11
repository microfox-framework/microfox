package ir.moke.microfox.jpa;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JpaFactory {
    private static final Logger logger = LoggerFactory.getLogger(JpaFactory.class);
    private static final Map<String, EntityManagerFactory> CONNECTION_FACTORY_MAP = new ConcurrentHashMap<>();
    private static final Map<String, MetadataSources> METADATA_SOURCES_MAP = new ConcurrentHashMap<>();
    private static final ScopedValue<EntityManager> ENTITY_MANAGER_SCOPED_VALUE = ScopedValue.newInstance();
    private static final Object validatorFactory;

    static {
        validatorFactory = getMicroFoxValidationFactory();
    }

    private JpaFactory() {
    }

    public static void register(HikariConfig hikariConfig, JpaConfig jpaConfig) {
        MetadataSources metadataSources = getMetadataSources(hikariConfig, jpaConfig);
        Metadata metadata = metadataSources.buildMetadata();
        SessionFactory sessionFactory = metadata.buildSessionFactory();
        EntityManagerFactory emf = sessionFactory.unwrap(EntityManagerFactory.class);
        CONNECTION_FACTORY_MAP.put(jpaConfig.getIdentity(), emf);
    }

    private static MetadataSources getMetadataSources(HikariConfig hikariConfig, JpaConfig jpaConfig) {
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
        Optional.ofNullable(jpaConfig.getDialect()).ifPresent(item -> standardServiceRegistryBuilder.applySetting("hibernate.dialect", jpaConfig.getDialect()));
        Optional.ofNullable(jpaConfig.getHbm2ddl()).ifPresent(item -> standardServiceRegistryBuilder.applySetting("hibernate.hbm2ddl.auto", jpaConfig.getHbm2ddl()));
        Optional.ofNullable(jpaConfig.getShowSql()).ifPresent(item -> standardServiceRegistryBuilder.applySetting("hibernate.show_sql", jpaConfig.getShowSql()));
        Optional.ofNullable(jpaConfig.getFormatSQL()).ifPresent(item -> standardServiceRegistryBuilder.applySetting("hibernate.format_sql", jpaConfig.getFormatSQL()));
        Optional.ofNullable(validatorFactory).ifPresent(item -> standardServiceRegistryBuilder.applySetting("jakarta.persistence.validation.factory", item));
        standardServiceRegistryBuilder.applySetting("hibernate.connection.datasource", dataSource);

        StandardServiceRegistry serviceRegistry = standardServiceRegistryBuilder.build();
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        List<String> packages = jpaConfig.getPackages();
        if (packages != null) {
            for (String p : packages) {
                Set<Class<?>> classes = scanEntities(p);
                metadataSources.addAnnotatedClasses(classes.toArray(Class[]::new));
            }
        }
        METADATA_SOURCES_MAP.put(jpaConfig.getIdentity(), metadataSources);
        return metadataSources;
    }

    private static Set<Class<?>> scanEntities(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return reflections.getTypesAnnotatedWith(Entity.class);
    }

    public static EntityManagerFactory getEntityManagerFactory(String identity) {
        return CONNECTION_FACTORY_MAP.get(identity);
    }

    public static ScopedValue<EntityManager> getEntityManagerScopedValue() {
        return ENTITY_MANAGER_SCOPED_VALUE;
    }

    public static MetadataSources getMetadataSources(String identity) {
        return METADATA_SOURCES_MAP.get(identity);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> repositoryInterface, EntityManager em) {
        return (T) Proxy.newProxyInstance(repositoryInterface.getClassLoader(), new Class<?>[]{repositoryInterface}, new RepositoryHandler(em));
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