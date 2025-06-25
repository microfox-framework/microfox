import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.kafka.KafkaFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static ir.moke.microfox.MicroFox.kafkaProducer;

public class KafkaTest {
    private static final String IDENTITY = "kafka-test";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9092;

    static {
        initialize();
    }

    private static void initialize() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "%s:%s".formatted(HOST, PORT));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaFactory.registerKafkaProducer(IDENTITY, properties, false);
    }

    public static void main(String[] args) {
        kafkaProducer(IDENTITY, KafkaTest::send);
    }

    private static void send(KafkaProducerController<String, String> kafkaProducerController) {
        kafkaProducerController.send("sample", null, null, null, "Hello mahdi", null);
        kafkaProducerController.send("sample", null, null, null, "Hello mahdi", null);
        kafkaProducerController.send("sample", null, null, null, "Hello mahdi", null);
    }
}
