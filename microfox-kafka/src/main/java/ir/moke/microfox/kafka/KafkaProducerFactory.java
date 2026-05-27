package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;

public class KafkaProducerFactory {
    private static final Map<String, KafkaProducer<?, ?>> PRODUCERS = new HashMap<>();
    private static final List<Map<String, Object>> CONFIGS = new ArrayList<>();

    public static <K, V> void register(Map<String, Object> configs) {
        Object clientID = configs.get(ProducerConfig.CLIENT_ID_CONFIG);
        if (clientID == null) throw new MicroFoxException("CLIENT_ID_CONFIG could not be null");
        boolean alreadyExists = isAlreadyExists(String.valueOf(clientID));
        if (alreadyExists) {
            throw new MicroFoxException("Producer %s already registered".formatted(clientID));
        }
        CONFIGS.add(configs);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaProducer<K, V> get(String clientId) {
        Map<String, Object> configs = getProperties(clientId);

        return (KafkaProducer<K, V>) PRODUCERS.computeIfAbsent(clientId, e -> {
            KafkaProducer<K, V> p = new KafkaProducer<>(configs);
            if (configs.containsKey(TRANSACTIONAL_ID_CONFIG)) p.initTransactions();
            return p;
        });
    }

    public static <K, V> void close(String clientId, Duration timeout) {
        Map<String, Object> configs = getProperties(clientId);
        KafkaProducer<K, V> producer = get(clientId);
        if (timeout != null) producer.close(timeout);
        else producer.close();
        CONFIGS.remove(configs);
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
                .map(item -> item.get(ProducerConfig.CLIENT_ID_CONFIG))
                .map(String::valueOf)
                .anyMatch(item -> item.equalsIgnoreCase(clientID));
    }

    private static Map<String, Object> getProperties(String clientId) {
        return CONFIGS.stream()
                .filter(item -> item.get(ProducerConfig.CLIENT_ID_CONFIG).equals(clientId))
                .findFirst()
                .orElseThrow(() -> new MicroFoxException("No kafka producer for clientId: " + clientId));
    }
}
