import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ir.moke.microfox.mybatis.MyBatisFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.h2.Driver;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class DB {

    public static void initializeMyBatis() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(Driver.class.getCanonicalName());
        hikariConfig.setJdbcUrl("jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("sa");
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        Environment environment = new Environment("h2", new JdbcTransactionFactory(), hikariDataSource);
        Configuration configuration = new Configuration();
        configuration.setEnvironment(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMappers("mapper");

        MyBatisFactory.configure(hikariDataSource, configuration);
    }

    public static void initializeDatabase() {
        try {
            DataSource dataSource = MyBatisFactory.getDatasourceMap("h2");
            Connection connection = dataSource.getConnection();
            List<String> lines = loadSqlScript();
            if (lines != null) {
                for (String line : lines) {
                    Statement statement = connection.createStatement();
                    statement.execute(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> loadSqlScript() {
        try (InputStream inputStream = MainClass.class.getClassLoader().getResourceAsStream("initialize_db.sql")) {
            if (inputStream != null) {
                String content = new String(inputStream.readAllBytes());
                return Arrays.stream(content.replaceAll("\\n", "").replaceAll("\\s+", " ").split(";")).toList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
