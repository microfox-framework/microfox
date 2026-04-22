package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaConsumerFactory {
    private static final Map<String, Map<String,Object>> CONFIGS = new HashMap<>();
    private static final Map<String, KafkaConsumer<?, ?>> CONSUMERS = new ConcurrentHashMap<>();

    public static void register(String identity, Map<String,Object> configs) {
        if (CONFIGS.containsKey(identity))
            throw new MicroFoxException("Consumer %s already registered".formatted(identity));
        CONFIGS.put(identity, configs);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaConsumer<K, V> get(String identity) {
        Map<String, Object> configs = CONFIGS.get(identity);
        if (configs == null) {
            throw new MicroFoxException("No Kafka producer for identity: " + identity);
        }
        return (KafkaConsumer<K, V>) CONSUMERS.computeIfAbsent(identity, id -> new KafkaConsumer<>(configs));
    }

    public static void close(String identity, Duration timeout) {
        KafkaConsumer<?, ?> consumer = CONSUMERS.remove(identity);
        if (consumer != null) {
            consumer.wakeup();
            if (timeout != null) consumer.close(timeout);
            else consumer.close();
        }
    }

    public static void closeAll() {
        CONSUMERS.keySet().forEach(item -> close(item, null));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaConsumerController<K, V> createProxyInstance(String identity) {
        return (KafkaConsumerController<K, V>) Proxy.newProxyInstance(
                KafkaConsumerController.class.getClassLoader(),
                new Class<?>[]{KafkaConsumerController.class},
                new KafkaConsumerHandler(identity)
        );
    }
}

