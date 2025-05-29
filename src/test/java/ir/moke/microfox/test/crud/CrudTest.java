package ir.moke.microfox.test.crud;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.persistence.BatisExecutor;
import ir.moke.microfox.test.crud.model.Person;
import ir.moke.microfox.test.crud.model.PersonMapper;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CrudTest {
    public static void main(String[] args) {
        DataSource dataSource = createDataSource();
        BatisExecutor.configure("h2", dataSource, "model");
        createTable(dataSource);

        Person person = new Person("mahdi");
        MicroFox.sql(PersonMapper.class, personMapper -> {
            personMapper.insert(person);
            return person;
        });

        System.out.println(">> ");

        List<Person> personList = MicroFox.sql(PersonMapper.class, PersonMapper::findAll);
        System.out.println(personList);
    }

    public static DataSource createDataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=ORACLE");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    public static void createTable(DataSource dataSource) {
        String sql = "CREATE TABLE person (id NUMBER PRIMARY KEY,name VARCHAR(255))";
        String sequence = "CREATE SEQUENCE PERSON_SEQ";

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            stmt.execute(sequence);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table", e);
        }
    }
}
