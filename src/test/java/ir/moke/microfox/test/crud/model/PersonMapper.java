package ir.moke.microfox.test.crud.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

public interface PersonMapper {

    @Insert("INSERT INTO person (id, name) VALUES (#{id}, #{name})")
    @SelectKey(statement = "SELECT person_seq.NEXTVAL FROM dual", keyProperty = "id", before = true, resultType = Long.class)
    void insert(Person person);

    @Select("select * from person")
    List<Person> findAll();
}
