import entity.Person;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import repository.PersonRepository;

import java.util.List;

import static ir.moke.microfox.MicroFox.jpa;

public class JpaTest {
    static {
        DB.initializeH2();
//        DB.initializePostgres();
    }

    public static void main(String[] args) {

        // Save single/batch
        jpa("h2", PersonRepository.class, TransactionPolicy.REQUIRED, JpaTest::saveItems);

        // Print all
        PersonRepository personRepository = jpa("h2", PersonRepository.class);
        List<Person> people = personRepository.find();
        System.out.println(people.size());

        Person person1 = people.getFirst();
        // Update
        person1.setName("Sina");
        person1.setFamily("Zoheir");
        jpa("h2", PersonRepository.class, pr -> pr.update(person1));

        // Delete item
        jpa("h2", PersonRepository.class, pr -> pr.delete(person1));

        // Criteria Query
        people = personRepository.find(null, null, null, 0, 100);
        people.forEach(System.out::println);
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
