import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ir.moke.microfox.jpa.JpaConfig;
import ir.moke.microfox.jpa.JpaFactory;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.List;

public class DB {

    public static void initializeH2() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(org.h2.Driver.class.getCanonicalName());
        hikariConfig.setJdbcUrl("jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("sa");
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        JpaConfig databaseConfig = new JpaConfig.Builder()
                .setPersistenceUnit("h2")
                .setProvider(new HibernatePersistenceProvider())
                .setHbm2ddl("update")
                .setDialect(H2Dialect.class.getCanonicalName())
                .setShowSql(true)
                .setPackages(List.of("entity"))
                .build();

        JpaFactory.register(hikariDataSource, databaseConfig);
    }


    public static void initializePostgres() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(org.postgresql.Driver.class.getCanonicalName());
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/admin");
        hikariConfig.setUsername("admin");
        hikariConfig.setPassword("adminpass");
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        JpaConfig databaseConfig = new JpaConfig.Builder()
                .setPersistenceUnit("postgres")
                .setProvider(new HibernatePersistenceProvider())
                .setHbm2ddl("update")
                .setDialect(PostgreSQLDialect.class.getCanonicalName())
                .setShowSql(true)
                .setPackages(List.of("entity"))
                .build();

        JpaFactory.register(hikariDataSource, databaseConfig);
    }
}
