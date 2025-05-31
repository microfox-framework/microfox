package ir.moke.microfox.persistence;

import org.apache.ibatis.session.*;

import java.util.HashMap;
import java.util.Map;

public class MicroFoxSQL {
    private static final Map<String, SqlSessionFactory> factories = new HashMap<>();

    public static <T> void configure(Configuration configuration) {
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
        String id = configuration.getEnvironment().getId();
        factories.put(id, factory);
    }

    public static SqlSessionFactory getSqlSessionFactory(String databaseId) {
        SqlSessionFactory factory = factories.get(databaseId);
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
}
