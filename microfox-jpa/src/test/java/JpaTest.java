import entity.Person;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import repository.PersonRepository;

import java.util.List;

import static ir.moke.microfox.MicroFox.jpa;
import static ir.moke.microfox.MicroFox.jpaTxRollback;

public class JpaTest {
    static {
        DB.initializeH2();
    }

    static void main() {

        // Save single/batch
        jpa("h2", PersonRepository.class, TransactionPolicy.REQUIRED, JpaTest::saveItems);

        // Print size
        PersonRepository personRepository = jpa("h2", PersonRepository.class);
        List<Person> people = personRepository.find();
        System.out.println("Person size: " + people.size());

        Person person1 = people.getFirst();
        Person person2 = people.get(1); //second person

        // Update
        person1.setName("Sina");
        person1.setFamily("Zoheir");
        jpa("h2", PersonRepository.class, pr -> pr.update(person1));
        System.out.println("Update person1: " + person1);

        // Delete item
        System.out.println("Delete person1 id: " + person1.getId());
        jpa("h2", PersonRepository.class, pr -> pr.delete(person1));

        // Rollback TX
        System.out.println("Delete person2 than rollback tx id: " + person2.getId());
        jpa("h2", PersonRepository.class, pr -> deletePerson2(pr, person2));

        // Criteria Query
        people = personRepository.find(null, null, null, 0, 100);
        people.forEach(System.out::println);
    }

    private static void deletePerson2(PersonRepository pr, Person person1) {
        pr.delete(person1);
        jpaTxRollback("h2");
    }

    private static void saveItems(PersonRepository personRepository) {
        Person person1 = new Person("Mahdi", "Sheikh Hosseini");
        Person person2 = new Person("Javad", "Alikhani");
        Person person3 = new Person("Ali", "Mohammadi");
        Person person4 = new Person("AAA", "BBB");

        personRepository.save(person1);
        personRepository.save(person2);
        jpa("h2", PersonRepository.class, TransactionPolicy.REQUIRED_NEW, repo -> repo.save(person3));
        jpa("h2", PersonRepository.class, TransactionPolicy.NOT_SUPPORTED, repo -> repo.save(person4));
    }
}
