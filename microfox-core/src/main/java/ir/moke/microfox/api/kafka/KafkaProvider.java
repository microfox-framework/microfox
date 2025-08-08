package ir.moke.microfox.api.kafka;

import java.util.function.Consumer;

public interface KafkaProvider {
    <K, V> void produce(String identity, Consumer<KafkaProducerController<K, V>> consumer);
    <K, V> void consumer(String identity, Consumer<KafkaConsumerController<K, V>> consumer);

    void stream(String identity, Consumer<KafkaStreamController> consumer);
}
