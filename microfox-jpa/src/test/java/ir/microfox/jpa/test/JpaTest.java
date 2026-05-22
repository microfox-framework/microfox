package ir.microfox.jpa.test;

import ir.microfox.jpa.test.entity.Person;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import ir.moke.microfox.jpa.OptionalRepository;

import java.util.List;

import static ir.moke.microfox.MicroFox.jpa;
import static ir.moke.microfox.MicroFox.jpaTxRollback;

public class JpaTest {
    private static final OptionalRepository<Person> personRepository = OptionalRepository.of("h2", Person.class);

    static {
        DB.initializeH2();
//        DB.initializePostgres();
    }

    static void main() {

        // Save single/batch
        jpa("h2", TransactionPolicy.REQUIRED, JpaTest::saveItems);

        // Print size
        List<Person> people = personRepository.find();
        System.out.println("Person size: " + people.size());

        Person person1 = people.getFirst();
        Person person2 = people.get(1); //second person

        // Update
        person1.setName("Sina");
        person1.setFamily("Zoheir");
        personRepository.update(person1);
        System.out.println("Update person1: " + person1);

        // Delete item
        System.out.println("Delete person1 id: " + person1.getId());
        personRepository.remove(person1);

        // Rollback TX
        System.out.println("Delete person2 than rollback tx id: " + person2.getId());
        jpa("h2", () -> deletePerson2(person2));

        // Criteria Query
        Long count = personRepository.count();
        System.out.println("Count : " + count);

        people = personRepository.find();
        people.forEach(System.out::println);
    }

    private static void deletePerson2(Person person1) {
        personRepository.remove(person1);
        jpaTxRollback("h2");
    }

    private static void saveItems() {
        Person person1 = new Person("Mahdi", "Sheikh Hosseini");
        Person person2 = new Person("Javad", "Alikhani");
        Person person3 = new Person("Ali", "Mohammadi");
        Person person4 = new Person("AAA", "BBB");

        personRepository.save(person1);
        personRepository.save(person2);
        jpa("h2", TransactionPolicy.REQUIRED_NEW, repo -> repo.persist(person3));
        jpa("h2", TransactionPolicy.NOT_SUPPORTED, repo -> repo.persist(person4));
    }
}
