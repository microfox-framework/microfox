package ir.moke.microfox.mybatis;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class MyBatisFactory {
    private static final Map<String, SqlSessionFactory> factoriesMap = new HashMap<>();
    private static final Map<String, DataSource> datasourceMap = new HashMap<>();

    public static void configure(DataSource dataSource, Configuration configuration) {
        String identity = configuration.getEnvironment().getId();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment(identity, transactionFactory, dataSource);

        configuration.setEnvironment(environment);

        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
        factoriesMap.put(identity, factory);
        datasourceMap.put(identity, dataSource);
    }

    public static SqlSessionFactory getSqlSessionFactory(String databaseId) {
        SqlSessionFactory factory = factoriesMap.get(databaseId);
        if (factory == null) {
            throw new IllegalStateException("MyBatis not configured for dbKey: " + databaseId);
        }
        return factory;
    }

    public static SqlSession getBatchSession(String dbKey) {
        return getSqlSessionFactory(dbKey).openSession(ExecutorType.BATCH, false);
    }

    public static SqlSession getSession(String dbKey) {
        return getSqlSessionFactory(dbKey).openSession(true);
    }

    public static DataSource getDatasourceMap(String identity) {
        return datasourceMap.get(identity);
    }
}
