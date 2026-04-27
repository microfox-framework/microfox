package ir.microfox.jpa.test;

import ir.microfox.jpa.test.entity.Person;
import ir.microfox.jpa.test.repository.PersonRepository;
import ir.moke.microfox.api.jpa.TransactionPolicy;

import static ir.moke.microfox.MicroFox.jpa;

public class MultipleOperationTest {
    static {
        DB.initializeH2();
//        DB.initializePostgres();
    }

    static void main() {
        Person person = new Person("Mahdi", "Sheikh Hosseini");

        // Save single
        jpa("h2", PersonRepository.class, TransactionPolicy.REQUIRED, repo -> repo.save(person));

        System.out.println("Person ID : " + person.getId());

        // set new values
        person.setName("Sina");
        person.setFamily("Zoheir");

        // Update and save new item
        jpa("h2", PersonRepository.class, pr -> {
            pr.update(person);
            pr.save(new Person("zzz", "zzz"));
        });

        System.out.println(jpa("h2", PersonRepository.class).find());
    }
}
