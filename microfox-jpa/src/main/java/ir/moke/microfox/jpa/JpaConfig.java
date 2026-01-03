package ir.moke.microfox.jpa;

import jakarta.persistence.spi.PersistenceProvider;

import java.util.List;

public class JpaConfig {
    private final String identity;
    private final PersistenceProvider provider;
    private final List<String> packages;
    private final String dialect;
    private final Boolean showSql;
    private final Boolean formatSQL;
    private final Boolean autoCommit;
    private final String hbm2ddl;

    private JpaConfig(Builder builder) {
        this.identity = builder.persistenceUnit;
        this.dialect = builder.dialect;
        this.showSql = builder.showSql;
        this.formatSQL = builder.formatSQL;
        this.autoCommit = builder.autoCommit;
        this.hbm2ddl = builder.hbm2ddl;
        this.provider = builder.provider;
        this.packages = builder.packages;
    }

    public String getIdentity() {
        return identity;
    }

    public PersistenceProvider getProvider() {
        return provider;
    }

    public List<String> getPackages() {
        return packages;
    }

    public String getDialect() {
        return dialect;
    }

    public Boolean getShowSql() {
        return showSql;
    }

    public Boolean getFormatSQL() {
        return formatSQL;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public String getHbm2ddl() {
        return hbm2ddl;
    }

    public static class Builder {
        private String persistenceUnit;
        private PersistenceProvider provider;
        private List<String> packages;
        private String dialect;
        private Boolean showSql;
        private Boolean formatSQL;
        private Boolean autoCommit;
        private String hbm2ddl;

        public Builder setPersistenceUnit(String persistenceUnit) {
            this.persistenceUnit = persistenceUnit;
            return this;
        }

        public Builder setProvider(PersistenceProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder setPackages(List<String> packages) {
            this.packages = packages;
            return this;
        }

        public Builder setDialect(String dialect) {
            this.dialect = dialect;
            return this;
        }

        public Builder setShowSql(Boolean showSql) {
            this.showSql = showSql;
            return this;
        }

        public Builder setFormatSQL(Boolean formatSQL) {
            this.formatSQL = formatSQL;
            return this;
        }

        public Builder setAutoCommit(Boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }

        public Builder setHbm2ddl(String hbm2ddl) {
            this.hbm2ddl = hbm2ddl;
            return this;
        }

        public JpaConfig build() {
            return new JpaConfig(this);
        }
    }
}
