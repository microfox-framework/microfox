package ir.microfox.jpa.test;

import ch.qos.logback.classic.Level;
import ir.microfox.jpa.test.entity.Person;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ir.microfox.jpa.test.repository.PersonRepository;

import static ir.moke.microfox.MicroFox.jpa;

public class StaticJpaTest {
    private static final Logger log = LoggerFactory.getLogger(StaticJpaTest.class);

    static class Service {
        private static final Logger logger = LoggerFactory.getLogger(Service.class);
        private static final PersonRepository repo = jpa("h2", PersonRepository.class);

        public static void save(Person person) {
            logger.info("Try to save person");
            repo.save(person);
            logger.info("Save person with id: {}", person.getId());
        }

        public static void printPerson() {
            repo.find().forEach(System.out::println);
        }
    }

    static {
        MicroFox.logger(new ConsoleGenericModel("jpa", "ir.microfox.jpa.test", Level.TRACE));
        DB.initializeH2();
    }

    static void main() {
        MicroFox.jpa("h2", () -> {
            Person p = new Person("Mahdi", "Sheikh Hosseini");
            Service.save(p);
            Service.printPerson();
        });
    }
}
