import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ir.moke.microfox.jpa.JpaConfig;
import ir.moke.microfox.jpa.JpaFactory;
import org.h2.Driver;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.List;

public class DB {

    public static void initializeJPA() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(Driver.class.getCanonicalName());
        hikariConfig.setJdbcUrl("jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("sa");
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        JpaConfig databaseConfig = new JpaConfig.Builder()
                .setPersistenceUnit("h2")
                .setProvider(new HibernatePersistenceProvider())
                .setHbm2ddl("update")
                .setDialect(H2Dialect.class)
                .setShowSql(true)
                .setPackages(List.of("entity"))
                .build();

        JpaFactory.register(hikariDataSource, databaseConfig);
    }
}
