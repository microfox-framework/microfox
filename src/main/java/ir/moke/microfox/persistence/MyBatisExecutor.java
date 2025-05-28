package ir.moke.microfox.persistence;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class MyBatisExecutor {

    private static SqlSessionFactory sqlSessionFactory;

    public static <T> void configure(String identity, DataSource dataSource, Class<T> mapperClass) {
        Environment environment = new Environment(identity, new JdbcTransactionFactory(), dataSource);
        Configuration config = new Configuration(environment);
        config.addMapper(mapperClass);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
    }

    public static <T> void configure(String identity, DataSource dataSource, String packageName) {
        Environment environment = new Environment(identity, new JdbcTransactionFactory(), dataSource);
        Configuration config = new Configuration(environment);
        config.addMappers(packageName);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
    }

    public static <T> T mapper(Class<T> mapperClass) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            return session.getMapper(mapperClass);
        }
    }

    public static SqlSession getBatchSession() {
        if (sqlSessionFactory == null) {
            throw new IllegalStateException("MyBatis is not configured");
        }
        return sqlSessionFactory.openSession(ExecutorType.BATCH, false);
    }
}
