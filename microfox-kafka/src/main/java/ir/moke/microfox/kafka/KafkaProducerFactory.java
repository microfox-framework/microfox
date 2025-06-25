package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.exception.MicrofoxException;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;

public class KafkaProducerFactory {
    private static final Map<String, Properties> KAFKA_PRODUCER_MAP = new HashMap<>();

    public static <K, V> void register(String identity, Properties properties, boolean transactional) {
        if (KAFKA_PRODUCER_MAP.containsKey(identity))
            throw new MicrofoxException("Producer %s already registered".formatted(identity));
        if (transactional) properties.setProperty(TRANSACTIONAL_ID_CONFIG, "tx-" + identity);
        KAFKA_PRODUCER_MAP.put(identity, properties);
    }

    public static <K, V> KafkaProducer<K, V> get(String identity) {
        Properties properties = KAFKA_PRODUCER_MAP.get(identity);
        if (properties == null) throw new MicrofoxException("No Kafka producer for identity: " + identity);
        KafkaProducer<K, V> kafkaProducer = new KafkaProducer<>(properties);
        if (properties.contains(TRANSACTIONAL_ID_CONFIG)) kafkaProducer.initTransactions();

        return kafkaProducer;
    }

    @SuppressWarnings("unchecked")
    static <K, V> KafkaProducerController<K, V> createProxyInstance(String identity) {
        return (KafkaProducerController<K, V>) Proxy.newProxyInstance(
                KafkaProducerController.class.getClassLoader(),
                new Class<?>[]{KafkaProducerController.class},
                new KafkaProducerHandler(identity)
        );
    }
}
