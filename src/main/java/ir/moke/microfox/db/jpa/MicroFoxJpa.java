package ir.moke.microfox.db.jpa;


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

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MicroFoxJpa {
    private static final Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORY_MAP = new ConcurrentHashMap<>();
    private static final Map<String, MetadataSources> METADATA_SOURCES_MAP = new ConcurrentHashMap<>();

    public static void createConnectionPoolEntityManagerFactory(HikariConfig hikariConfig, JpaConfig jpaConfig) {
        MetadataSources metadataSources = getMetadataSources(hikariConfig, jpaConfig);
        Metadata metadata = metadataSources.buildMetadata();
        SessionFactory sessionFactory = metadata.buildSessionFactory();
        EntityManagerFactory emf = sessionFactory.unwrap(EntityManagerFactory.class);
        ENTITY_MANAGER_FACTORY_MAP.put(jpaConfig.getPersistenceUnit(), emf);
    }

    private static MetadataSources getMetadataSources(HikariConfig hikariConfig, JpaConfig jpaConfig) {
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
        Optional.ofNullable(jpaConfig.getDialect()).ifPresent(item -> standardServiceRegistryBuilder.applySetting("hibernate.dialect", jpaConfig.getDialect()));
        Optional.ofNullable(jpaConfig.getHbm2ddl()).ifPresent(item -> standardServiceRegistryBuilder.applySetting("hibernate.hbm2ddl.auto", jpaConfig.getHbm2ddl()));
        Optional.ofNullable(jpaConfig.getShowSql()).ifPresent(item -> standardServiceRegistryBuilder.applySetting("hibernate.show_sql", jpaConfig.getShowSql()));
        Optional.ofNullable(jpaConfig.getFormatSQL()).ifPresent(item -> standardServiceRegistryBuilder.applySetting("hibernate.format_sql", jpaConfig.getFormatSQL()));
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
        METADATA_SOURCES_MAP.put(jpaConfig.getPersistenceUnit(), metadataSources);
        return metadataSources;
    }

    private static Set<Class<?>> scanEntities(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return reflections.getTypesAnnotatedWith(Entity.class);
    }

    public static EntityManager getEntityManager(String unit) {
        EntityManagerFactory emf = ENTITY_MANAGER_FACTORY_MAP.get(unit);
        if (emf == null) {
            throw new IllegalArgumentException("No EntityManagerFactory registered for unit: " + unit);
        }
        return emf.createEntityManager();
    }


    public static MetadataSources getMetadataSources(String persistenceUnitName) {
        return METADATA_SOURCES_MAP.get(persistenceUnitName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> repositoryInterface, String persistenceUnitName) {
        return (T) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class<?>[]{repositoryInterface},
                new RepositoryHandler(persistenceUnitName)
        );
    }
}

