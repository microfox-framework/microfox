package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.exception.MicrofoxException;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaConsumerFactory {
    private static final Map<String, Properties> CONFIGS = new HashMap<>();
    private static final Map<String, KafkaConsumer<?, ?>> CONSUMERS = new ConcurrentHashMap<>();

    public static void register(String identity, Properties properties) {
        if (CONFIGS.containsKey(identity))
            throw new MicrofoxException("Consumer %s already registered".formatted(identity));
        CONFIGS.put(identity, properties);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> KafkaConsumer<K, V> get(String identity) {
        Properties props = CONFIGS.get(identity);
        if (props == null) {
            throw new MicrofoxException("No Kafka producer for identity: " + identity);
        }
        return (KafkaConsumer<K, V>) CONSUMERS.computeIfAbsent(identity, id -> new KafkaConsumer<>(props));
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

