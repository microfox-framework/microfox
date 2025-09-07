package repository;

import entity.Person;
import ir.moke.microfox.jpa.annotation.*;

import java.util.List;

public interface PersonRepository {
    void save(Person person);

    List<Person> find(int offset, int size);

    List<Person> find();

    Person find(long id);

    Person findByName(String name);

    void update(Person person);

    Person findById(long id);

    void delete(Person person);

    @Criteria(provider = PersonCriteriaProvider.class, ignoreNullValues = true)
    List<Person> find(Long id,
                      String name,
                      String family,
                      int offset,
                      int maxResult);
}
