package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.*;

import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;

public class KafkaProducerFactory {
    private static final Map<String, KafkaProducer<?, ?>> PRODUCERS = new HashMap<>();
    private static final List<Properties> CONFIGS = new ArrayList<>();

    public static <K, V> void register(Properties properties) {
        String clientID = properties.getProperty(ProducerConfig.CLIENT_ID_CONFIG);
        boolean alreadyExists = isAlreadyExists(clientID);
        if (alreadyExists) {
            throw new MicroFoxException("Producer %s already registered".formatted(clientID));
        }
        CONFIGS.add(properties);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaProducer<K, V> get(String clientId) {
        Properties props = getProperties(clientId);

        return (KafkaProducer<K, V>) PRODUCERS.computeIfAbsent(clientId, e -> {
            KafkaProducer<K, V> p = new KafkaProducer<>(props);
            if (props.containsKey(TRANSACTIONAL_ID_CONFIG)) p.initTransactions();
            return p;
        });
    }

    public static <K, V> void close(String clientId, Duration timeout) {
        Properties properties = getProperties(clientId);
        KafkaProducer<K, V> producer = get(clientId);
        if (timeout != null) producer.close(timeout);
        else producer.close();
        CONFIGS.remove(properties);
    }

    @SuppressWarnings("unchecked")
    static <K, V> KafkaProducerController<K, V> createProxyInstance(String identity) {
        return (KafkaProducerController<K, V>) Proxy.newProxyInstance(
                KafkaProducerController.class.getClassLoader(),
                new Class<?>[]{KafkaProducerController.class},
                new KafkaProducerHandler(identity)
        );
    }

    private static boolean isAlreadyExists(String clientID) {
        return CONFIGS.stream()
                .map(item -> item.getProperty(ProducerConfig.CLIENT_ID_CONFIG))
                .anyMatch(item -> item.equalsIgnoreCase(clientID));
    }

    private static Properties getProperties(String clientId) {
        Properties props = CONFIGS.stream()
                .filter(item -> item.getProperty(ProducerConfig.CLIENT_ID_CONFIG).equals(clientId))
                .findFirst()
                .orElse(null);
        if (props == null) {
            throw new MicroFoxException("No Kafka producer for clientId: " + clientId);
        }
        return props;
    }
}
