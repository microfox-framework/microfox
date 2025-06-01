package ir.moke.microfox.persistence;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MicroFoxJpa {
    private static final Map<String, EntityManagerFactory> emfMap = new ConcurrentHashMap<>();

    public static void createConnectionPoolEntityManagerFactory(MicroFoxDatabaseConfig config) {

        HikariDataSource dataSource = new HikariDataSource(getHikariConfig(config));

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.nonJtaDataSource", dataSource);

        EntityManagerFactory factory = Persistence.createEntityManagerFactory(config.persistenceUnit(), properties);
        emfMap.put(config.persistenceUnit(), factory);
    }

    public static EntityManager getEntityManager(String unit) {
        EntityManagerFactory emf = emfMap.get(unit);
        if (emf == null) {
            throw new IllegalArgumentException("No EntityManagerFactory registered for unit: " + unit);
        }
        return emf.createEntityManager();
    }

    public static HikariConfig getHikariConfig(MicroFoxDatabaseConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(config.driver());
        hikariConfig.setJdbcUrl(config.url());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());

        // Optional pool settings
        hikariConfig.setMaximumPoolSize(config.maxPoolSize());
        hikariConfig.setMinimumIdle(config.minimumIdle());
        hikariConfig.setAutoCommit(config.autoCommit());

        return hikariConfig;
    }
}

