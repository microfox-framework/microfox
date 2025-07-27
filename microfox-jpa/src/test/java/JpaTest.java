import entity.Person;
import repository.PersonRepository;

import java.util.List;

import static ir.moke.microfox.MicroFox.jpa;
import static ir.moke.microfox.MicroFox.jpaTx;

public class JpaTest {
    static {
        DB.initializeJPA();
    }

    public static void main(String[] args) {

        Person person1 = new Person("Mahdi", "Sheikh Hosseini");
        Person person2 = new Person("Javad", "Alikhani");
        Person person3 = new Person("Ali", "Mohammadi");

        // Save single/batch
        jpaTx("h2", PersonRepository.class, personRepository -> {
            personRepository.save(person1);
            personRepository.save(person2);
            personRepository.save(person3);
        });

        // Print all
        PersonRepository personRepository = jpa("h2", PersonRepository.class);
        personRepository.find().forEach(System.out::println);

        // Update
        person1.setName("Sina");
        person1.setFamily("Zoheir");
        jpaTx("h2", PersonRepository.class, pr -> pr.update(person1));

        // Delete item
        jpaTx("h2", PersonRepository.class, pr -> pr.delete(person1));

        // Criteria Query
        List<Person> people = personRepository.find(2L, null, null, 0, 100);
        people.forEach(System.out::println);
    }
}
