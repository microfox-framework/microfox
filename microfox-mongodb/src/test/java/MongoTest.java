import com.mongodb.client.MongoCollection;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.mongodb.MongoConnectionInfo;
import ir.moke.microfox.mongodb.MongoFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MongoTest {
    private static final String IDENTITY = "mongo-db";

    @BeforeAll
    public static void init() {
        MongoConnectionInfo info = new MongoConnectionInfo("admin",
                "adminpass",
                "127.0.0.1",
                27017,
                "test",
                "authSource=admin");
        MongoFactory.registerMongoDatabase(IDENTITY, info);
    }

    @Test
    public void checkInsert() {
        Person p = new Person("Mahdi", "Sheikh Hosseini", 12L);
        MongoCollection<Person> collection = MicroFox.mongo(IDENTITY, Person.class);
        collection.insertOne(p);
    }
}
