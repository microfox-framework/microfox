package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.clients.consumer.CloseOptions;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerFactory {
    private static final Map<String, KafkaConsumer<?, ?>> CONSUMERS = new HashMap<>();
    private static final Map<String, Map<String, Object>> CONFIGS = new HashMap<>();

    public static void register(String identity, Map<String, Object> configs) {
        if (identity == null)
            throw new MicroFoxException("identity could not be null");
        if (configs == null)
            throw new MicroFoxException("config map could not be null");
        if (isAlreadyExists(identity)) {
            throw new MicroFoxException("Consumer %s already registered".formatted(identity));
        }
        CONFIGS.put(identity, configs);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaConsumer<K, V> get(String identity) {
        Map<String, Object> configs = getConfig(identity);
        return (KafkaConsumer<K, V>) CONSUMERS.computeIfAbsent(identity, id -> new KafkaConsumer<>(configs));
    }

    public static void close(String identity, Duration timeout) {
        KafkaConsumer<?, ?> consumer = CONSUMERS.remove(identity);
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

    private static boolean isAlreadyExists(String identity) {
        return CONFIGS.containsKey(identity);
    }

    private static Map<String, Object> getConfig(String clientId) {
        Map<String, Object> config = CONFIGS.get(clientId);
        if (config == null) throw new MicroFoxException("No kafka consumer for clientId: " + clientId);
        return config;
    }
}

