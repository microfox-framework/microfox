import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.elastic.BulkActionType;
import ir.moke.microfox.api.elastic.BulkOperation;
import ir.moke.microfox.api.elastic.ElasticCriteria;
import ir.moke.microfox.api.elastic.ElasticRepository;
import ir.moke.microfox.elastic.ElasticConfig;
import ir.moke.microfox.elastic.ElasticFactory;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ElasticTest {

    @BeforeAll
    public static void init() {
        ElasticConfig config = new ElasticConfig("127.0.0.1", 9200, "admin", "adminpass", false);
        ElasticFactory.register("el", config);
    }

    @Test
    @Order(0)
    public void indexCreate() {
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        elasticRepository.indexCreate();
    }

    @Test
    @Order(1)
    public void indexRefresh() {
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        elasticRepository.indexRefresh("person");
    }

    @Test
    @Order(2)
    public void indexDelete() {
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        elasticRepository.indexDelete("person");
    }

    @Test
    @Order(3)
    public void checkSave() {
        Person person = new Person(1, "Mahdi", "Sheikh Hosseini", 23);
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        elasticRepository.save("a1", person);
    }

    @Test
    @Order(4)
    public void checkGet() {
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        Person person = elasticRepository.get("a1");
        System.out.println(person);
    }

    @Test
    @Order(5)
    public void checkUpdate() {
        Person person = new Person(1, "Javad", "Mohammadi", 12);
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        elasticRepository.update("a1", person);
    }

    @Test
    @Order(6)
    public void checkSearch() {
        ElasticCriteria criteria = ElasticCriteria.builder()
                .match("name", "javad")
                .build();
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        List<Person> list = elasticRepository.search(criteria);
        System.out.println(list);
    }

    @Test
    @Order(7)
    public void checkDelete() {
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        elasticRepository.delete("a1");
    }

    @Test
    @Order(8)
    public void checkDeleteByQuery() {
        ElasticCriteria criteria = ElasticCriteria.builder().match("name", "Mahdi").build();
        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        elasticRepository.deleteByQuery(criteria);
    }

    @Test
    @Order(9)
    public void checkBulk() {
        Person p1 = new Person(1,"mahdi","Sheikh Hosseini",33);
        Person p2 = new Person(2,"Ali","Mohammadi",33);
        Person p3 = new Person(3,"Vahid","Jafari",33);
        BulkOperation<Person> bulkOperation1 = new BulkOperation<>(BulkActionType.SAVE,"1",p1);
        BulkOperation<Person> bulkOperation2 = new BulkOperation<>(BulkActionType.SAVE,"2",p2);
        BulkOperation<Person> bulkOperation3 = new BulkOperation<>(BulkActionType.SAVE,"3",p3);

        ElasticRepository<Person> elasticRepository = MicroFox.elastic("el", Person.class);
        elasticRepository.bulk(List.of(bulkOperation1,bulkOperation2,bulkOperation3));
    }
}
