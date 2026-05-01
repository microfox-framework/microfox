package ir.microfox.jpa.test;

import com.zaxxer.hikari.hibernate.HikariConnectionProvider;
import ir.moke.microfox.MicroFox;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQLDialect;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DB {

    public static void initializeH2() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        properties.put(AvailableSettings.JAKARTA_JDBC_DRIVER, org.h2.Driver.class.getCanonicalName());
        properties.put(AvailableSettings.JAKARTA_JDBC_USER, "sa");
        properties.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, "sa");
        properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
        properties.put(AvailableSettings.DIALECT, H2Dialect.class.getCanonicalName());
        properties.put(AvailableSettings.SHOW_SQL, "true");

        MicroFox.jpaRegisterWithPackage("h2", Set.of("ir.microfox.jpa.test.entity"), properties);
    }


    public static void initializePostgres() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:postgresql://localhost:5432/admin");
        properties.put(AvailableSettings.JAKARTA_JDBC_DRIVER, org.postgresql.Driver.class.getCanonicalName());
        properties.put(AvailableSettings.JAKARTA_JDBC_USER, "admin");
        properties.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, "adminpass");
        properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
        properties.put(AvailableSettings.DIALECT, PostgreSQLDialect.class.getCanonicalName());
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.put(AvailableSettings.CONNECTION_PROVIDER, HikariConnectionProvider.class.getCanonicalName());
        properties.put(AvailableSettings.HIKARI_MAX_SIZE, "100");
        properties.put(AvailableSettings.HIKARI_MIN_IDLE_SIZE, "50");

        MicroFox.jpaRegisterWithPackage("postgres", Set.of("ir.microfox.jpa.test.entity"), properties);
    }
}
