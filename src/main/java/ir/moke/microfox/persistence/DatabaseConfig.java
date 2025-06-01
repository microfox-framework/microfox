package ir.moke.microfox.persistence;

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
}
