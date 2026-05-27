import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.kafka.KafkaProducerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;

import static ir.moke.microfox.MicroFox.kafkaProducer;

public class KafkaProducerTest {
    private static final String IDENTITY = "kafka-test";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9092;

    static {
        initializeProducer();
    }

    private static void initializeProducer() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "%s:%s".formatted(HOST, PORT));
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducerFactory.register(configs);
    }

    static void main() {
        kafkaProducer(IDENTITY, KafkaProducerTest::send);
    }

    private static void send(KafkaProducerController<String, String> kafkaProducerController) {
        kafkaProducerController.send("sample", "Hello mahdi");
        kafkaProducerController.close();
    }
}
