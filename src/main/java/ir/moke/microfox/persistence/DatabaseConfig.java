package ir.moke.microfox.persistence;

import com.zaxxer.hikari.HikariConfig;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record DatabaseConfig(String persistenceUnit,
                             String driver,
                             String dialect,
                             String url,
                             String username,
                             String password,
                             Boolean showSql,
                             Boolean formatSQL,
                             Boolean autoCommit,
                             String hbm2ddl,
                             Integer maxPoolSize,
                             Integer minimumIdle) {

    public Map<String, Object> getDatabaseProperties() {
        Map<String, Object> settings = new HashMap<>();
        Optional.ofNullable(driver).ifPresent(item -> settings.put("jakarta.persistence.jdbc.driver", item));
        Optional.ofNullable(dialect).ifPresent(item -> settings.put("hibernate.dialect", item));
        Optional.ofNullable(url).ifPresent(item -> settings.put("jakarta.persistence.jdbc.url", item));
        Optional.ofNullable(username).ifPresent(item -> settings.put("jakarta.persistence.jdbc.user", username));
        Optional.ofNullable(password).ifPresent(item -> settings.put("jakarta.persistence.jdbc.password", password));
        return settings;
    }

    public HikariConfig getHikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        // Optional pool settings
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setAutoCommit(autoCommit);

        return hikariConfig;
    }
}
