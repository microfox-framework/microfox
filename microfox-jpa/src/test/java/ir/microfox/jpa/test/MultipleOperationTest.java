package ir.microfox.jpa.test;

import ir.microfox.jpa.test.entity.Person;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import ir.moke.microfox.jpa.OptionalRepository;

import static ir.moke.microfox.MicroFox.jpa;

public class MultipleOperationTest {
    static {
        DB.initializeH2();
//        DB.initializePostgres();
    }

    static void main() {
        Person person = new Person("Mahdi", "Sheikh Hosseini");

        // Save single
        jpa("h2", TransactionPolicy.REQUIRED, em -> em.persist(person));

        System.out.println("Person ID : " + person.getId());

        // set new values
        person.setName("Sina");
        person.setFamily("Zoheir");

        // Update and save new item
        jpa("h2", em -> {
            em.merge(person);
            em.persist(new Person("zzz", "zzz"));
        });

        System.out.println(OptionalRepository.of("h2", Person.class).select());
    }
}
