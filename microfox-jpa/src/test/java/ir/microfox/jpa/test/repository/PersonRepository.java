package ir.microfox.jpa.test.repository;

import ir.microfox.jpa.test.entity.Person;
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
    List<Person> find(@QueryParameter("id") Long id,
                      @QueryParameter("name") String name,
                      @QueryParameter("family") String family,
                      @Offset int offset,
                      @MaxResults int maxResult);

    @Criteria(provider = PersonCriteriaProvider.class, ignoreNullValues = true)
    Long count(@QueryParameter("id") Long id,
               @QueryParameter("name") String name,
               @QueryParameter("family") String family);

    @Criteria(provider = PersonCriteriaProvider.class, ignoreNullValues = true)
    boolean exists(@QueryParameter("id") Long id,
                   @QueryParameter("name") String name,
                   @QueryParameter("family") String family);
}
