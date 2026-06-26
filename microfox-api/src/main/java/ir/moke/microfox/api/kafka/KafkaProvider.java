package ir.moke.microfox.api.kafka;

import java.util.Map;
import java.util.function.Consumer;

public interface KafkaProvider {

    void registerProducer(String identity, Map<String, Object> config);

    void registerConsumer(String identity, Map<String, Object> config);

    void registerStream(String identity, Map<String, Object> config);

    void unregisterProducer(String identity);

    void unregisterConsumer(String identity);

    void unregisterStream(String identity);

    <K, V> void produce(String identity, Consumer<KafkaProducerController<K, V>> consumer);

    <K, V> void consumer(String identity, Consumer<KafkaConsumerController<K, V>> consumer);

    void stream(String identity, Object topology, Consumer<KafkaStreamController> consumer);
}
