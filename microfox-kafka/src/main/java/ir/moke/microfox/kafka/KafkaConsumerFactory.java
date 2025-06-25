package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.exception.MicrofoxException;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumerFactory {
    private static final Map<String, Properties> CONSUMER_MAP = new HashMap<>();

    public static void register(String identity, Properties properties) {
        if (CONSUMER_MAP.containsKey(identity))
            throw new MicrofoxException("Consumer %s already registered".formatted(identity));
        CONSUMER_MAP.put(identity, properties);
    }

    public static <K, V> KafkaConsumer<K, V> get(String identity) {
        Properties properties = CONSUMER_MAP.get(identity);
        if (properties == null) throw new MicrofoxException("No Kafka consumer for identity: " + identity);
        return new KafkaConsumer<>(properties);
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

