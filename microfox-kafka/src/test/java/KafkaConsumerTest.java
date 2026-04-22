import ch.qos.logback.classic.Level;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.kafka.KafkaConsumerFactory;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ir.moke.microfox.MicroFox.kafkaConsumer;

public class KafkaConsumerTest {
    private static final String IDENTITY = "kafka-test";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9092;

    static {
        MicroFox.logger(new ConsoleGenericModel("Kafka", "ir.moke.microfox.kafka", Level.TRACE));
        initializeConsumer();
    }

    private static void initializeConsumer() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "%s:%s".formatted(HOST, PORT));
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "G1");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        KafkaConsumerFactory.register(IDENTITY, configs);
    }

    static void main() {
        kafkaConsumer(IDENTITY, KafkaConsumerTest::listen);
    }

    private static void listen(KafkaConsumerController<String, String> kafkaConsumerController) {
        kafkaConsumerController.listen(List.of("sample"), (topic, key, value, partition, offset, timestamp, serializedKeySize, serializedValueSize, headers, leaderEpoch, deliveryCount) -> System.out.printf("Key: %s   | Value: %s%n", key, value));
    }
}
