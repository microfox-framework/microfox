package ir.microfox.jpa.test;

import ir.microfox.jpa.test.entity.Person;
import ir.moke.microfox.jpa.OptionalRepository;

import static ir.moke.microfox.MicroFox.jpa;

public class UniqueTest {
    static {
        DB.initializeH2();
//        DB.initializePostgres();
    }

    private static final OptionalRepository<Person> repo = OptionalRepository.of("h2", Person.class);

    static void main() {
        jpa("h2", UniqueTest::save);
        jpa("h2", UniqueTest::save);
    }

    public static void save() {
        try {
            Person person = new Person("Mahdi", "Sheikh Hosseini");
            repo.save(person);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
