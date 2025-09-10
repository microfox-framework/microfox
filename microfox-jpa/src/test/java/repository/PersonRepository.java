package repository;

import entity.Person;
import ir.moke.microfox.jpa.annotation.*;

import java.util.List;

public interface PersonRepository {
    @Persist
    void save(Person person);

    @Query(value = "select p from Person p")
    List<Person> find(int offset, int size);

    @Query(value = "select p from Person p")
    List<Person> find();

    @Find
    Person find(long id);

    @Query(value = "select p from Person p where p.name=:name")
    Person findByName(@QueryParameter("name") String name);

    @Merge
    void update(Person person);

    @Find
    Person findById(long id);

    @Remove
    void delete(Person person);

    @Criteria(provider = PersonCriteriaProvider.class, ignoreNullValues = true)
    List<Person> find(Long id,
                      String name,
                      String family,
                      int offset,
                      int maxResult);
}
