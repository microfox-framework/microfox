package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.clients.consumer.CloseOptions;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;

public class KafkaConsumerFactory {
    private static final Map<String, KafkaConsumer<?, ?>> CONSUMERS = new ConcurrentHashMap<>();
    private static final List<Map<String, Object>> CONFIGS = new ArrayList<>();

    public static void register(Map<String, Object> configs) {
        Object clientID = configs.get(ProducerConfig.CLIENT_ID_CONFIG);
        if (clientID == null) throw new MicroFoxException("CLIENT_ID_CONFIG could not be null");

        boolean alreadyExists = isAlreadyExists(String.valueOf(clientID));
        if (alreadyExists) {
            throw new MicroFoxException("Consumer %s already registered".formatted(clientID));
        }

        CONFIGS.add(configs);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaConsumer<K, V> get(String clientId) {
        Map<String, Object> configs = getProperties(clientId);
        return (KafkaConsumer<K, V>) CONSUMERS.computeIfAbsent(clientId, id -> new KafkaConsumer<>(configs));
    }

    public static void close(String clientId, Duration timeout) {
        KafkaConsumer<?, ?> consumer = CONSUMERS.remove(clientId);
        if (consumer != null) {
            consumer.wakeup();
            if (timeout != null) consumer.close(CloseOptions.timeout(timeout));
            else consumer.close();
        }
    }

    public static void closeAll() {
        CONSUMERS.keySet().forEach(item -> close(item, null));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaConsumerController<K, V> createProxyInstance(String clientId) {
        return (KafkaConsumerController<K, V>) Proxy.newProxyInstance(
                KafkaConsumerController.class.getClassLoader(),
                new Class<?>[]{KafkaConsumerController.class},
                new KafkaConsumerHandler(clientId)
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
                .orElseThrow(() -> new MicroFoxException("No kafka consumer for clientId: " + clientId));
    }
}

