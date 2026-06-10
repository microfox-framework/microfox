package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;

public class KafkaProducerFactory {
    private static final Map<String, KafkaProducer<?, ?>> PRODUCERS = new HashMap<>();
    private static final Map<String, Map<String, Object>> CONFIGS = new HashMap<>();

    public static <K, V> void register(String identity, Map<String, Object> configs) {
        if (identity == null)
            throw new MicroFoxException("identity could not be null");
        if (configs == null)
            throw new MicroFoxException("config map could not be null");
        if (isAlreadyExists(identity))
            throw new MicroFoxException("Producer %s already registered".formatted(identity));
        CONFIGS.put(identity, configs);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaProducer<K, V> get(String identity) {
        Map<String, Object> configs = getConfig(identity);

        return (KafkaProducer<K, V>) PRODUCERS.computeIfAbsent(identity, e -> {
            KafkaProducer<K, V> p = new KafkaProducer<>(configs);
            if (configs.containsKey(TRANSACTIONAL_ID_CONFIG)) p.initTransactions();
            return p;
        });
    }

    public static <K, V> void close(String identity, Duration timeout) {
        Map<String, Object> configs = getConfig(identity);
        KafkaProducer<K, V> producer = get(identity);
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

    private static boolean isAlreadyExists(String identity) {
        return CONFIGS.containsKey(identity);
    }

    private static Map<String, Object> getConfig(String clientId) {
        Map<String, Object> config = CONFIGS.get(clientId);
        if (config == null) throw new MicroFoxException("No kafka producer for clientId: " + clientId);
        return config;
    }
}
