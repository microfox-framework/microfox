package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.api.kafka.KafkaProvider;
import ir.moke.microfox.api.kafka.KafkaStreamController;

import java.util.function.Consumer;

public class KafkaProviderImpl implements KafkaProvider {

    @Override
    public <K, V> void produce(String identity, Consumer<KafkaProducerController<K, V>> consumer) {
        KafkaProducerController<K, V> controller = KafkaProducerFactory.createProxyInstance(identity);
        consumer.accept(controller);
    }

    @Override
    public <K, V> void consumer(String identity, Consumer<KafkaConsumerController<K, V>> consumer) {
        KafkaConsumerController<K, V> controller = KafkaConsumerFactory.createProxyInstance(identity);
        consumer.accept(controller);
    }

    @Override
    public void stream(String identity, Consumer<KafkaStreamController> consumer) {
        KafkaStreamController kafkaStreamController = KafkaStreamFactory.createProxyInstance(identity);
        consumer.accept(kafkaStreamController);
    }
}
