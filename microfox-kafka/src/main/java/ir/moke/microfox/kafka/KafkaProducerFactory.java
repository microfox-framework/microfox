package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.exception.MicrofoxException;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;

public class KafkaProducerFactory {
    private static final Map<String, KafkaProducer<?, ?>> PRODUCERS = new ConcurrentHashMap<>();
    private static final Map<String, Properties> CONFIGS = new ConcurrentHashMap<>();

    public static <K, V> void register(String identity, Properties properties, boolean transactional) {
        if (CONFIGS.putIfAbsent(identity, properties) != null) {
            throw new MicrofoxException("Producer %s already registered".formatted(identity));
        }
        if (transactional) {
            properties.putIfAbsent(TRANSACTIONAL_ID_CONFIG, "tx-" + identity);
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaProducer<K, V> get(String identity) {
        Properties props = CONFIGS.get(identity);
        if (props == null) {
            throw new MicrofoxException("No Kafka producer for identity: " + identity);
        }
        return (KafkaProducer<K, V>) PRODUCERS.computeIfAbsent(identity, id -> {
            KafkaProducer<?, ?> p = new KafkaProducer<>(props);
            if (props.containsKey(TRANSACTIONAL_ID_CONFIG)) p.initTransactions();
            return p;
        });
    }

    public static void close(String identity, Duration timeout) {
        KafkaProducer<?, ?> p = PRODUCERS.remove(identity);
        if (p != null) {
            if (timeout != null) p.close(timeout);
            else p.close();
        }
    }

    public static void closeAll() {
        PRODUCERS.keySet().forEach(item -> close(item, null));
        PRODUCERS.clear();
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
