package ir.moke.microfox.jpa;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JpaFactory {
    private static final Logger logger = LoggerFactory.getLogger(JpaFactory.class);
    private static final Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORY_MAP = new ConcurrentHashMap<>();
    private static final Map<String, MetadataSources> METADATA_SOURCES_MAP = new ConcurrentHashMap<>();
    private static final ThreadLocal<Map<String, EntityManager>> CONTEXT = ThreadLocal.withInitial(HashMap::new);
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
        ENTITY_MANAGER_FACTORY_MAP.put(jpaConfig.getIdentity(), emf);
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

    public static EntityManager getEntityManager(String identity) {
        EntityManagerFactory emf = ENTITY_MANAGER_FACTORY_MAP.get(identity);
        if (emf == null) {
            throw new IllegalArgumentException("No EntityManagerFactory registered for unit name: " + identity);
        }

        Map<String, EntityManager> contextMap = CONTEXT.get();
        EntityManager em = contextMap.get(identity);

        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
            contextMap.put(identity, em);
        }

        return em;
    }

    public static void closeEntityManager(String identity) {
        Map<String, EntityManager> contextMap = CONTEXT.get();
        EntityManager em = contextMap.remove(identity);
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    public static void beginTx(String identity) {
        EntityTransaction tx = getEntityManager(identity).getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
    }

    public static void commitTx(String identity) {
        EntityTransaction tx = getEntityManager(identity).getTransaction();
        if (tx.isActive()) {
            tx.commit();
        }
    }

    public static void rollbackTx(String identity) {
        EntityTransaction tx = getEntityManager(identity).getTransaction();
        if (tx.isActive()) {
            tx.rollback();
        }
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

