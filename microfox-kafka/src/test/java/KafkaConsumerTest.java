import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.kafka.KafkaConsumerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.List;
import java.util.Properties;

import static ir.moke.microfox.MicroFox.kafkaConsumer;

public class KafkaConsumerTest {
    private static final String IDENTITY = "kafka-test";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9092;

    static {
        initializeConsumer();
    }

    private static void initializeConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "%s:%s".formatted(HOST, PORT));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "G1");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        KafkaConsumerFactory.register(IDENTITY, properties);
    }


    public static void main(String... str) {
        kafkaConsumer(IDENTITY, KafkaConsumerTest::getListen);
    }

    private static void getListen(KafkaConsumerController<String, String> kafkaConsumerController) {
        kafkaConsumerController.listen(List.of("sample"), (topic, key, value, partition, offset, timestamp, serializedKeySize, serializedValueSize, headers, leaderEpoch, deliveryCount) -> System.out.printf("Key: %s   | Value: %s%n", key, value));
    }
}
