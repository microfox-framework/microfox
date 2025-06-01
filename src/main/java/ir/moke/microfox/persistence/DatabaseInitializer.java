package ir.moke.microfox.persistence;


import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseInitializer {
    private static final Map<String, EntityManagerFactory> emfMap = new ConcurrentHashMap<>();

    public static EntityManagerFactory createConnectionPoolEntityManagerFactory(DatabaseConfig config) {

        HikariDataSource dataSource = new HikariDataSource(config.getHikariConfig());

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.nonJtaDataSource", dataSource);

        return Persistence.createEntityManagerFactory("your-persistence-unit-name", properties);
    }

    public static EntityManager getEntityManager(String unit) {
        return emfMap.get(unit).createEntityManager();
    }
}

