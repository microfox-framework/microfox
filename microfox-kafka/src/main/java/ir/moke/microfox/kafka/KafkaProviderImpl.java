package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.api.kafka.KafkaProvider;

import java.util.function.Consumer;

public class KafkaProviderImpl implements KafkaProvider {

    @Override
    public <K, V> void produce(String identity, Consumer<KafkaProducerController<K, V>> consumer) {
        KafkaProducerController<K, V> controller = KafkaFactory.createProxyInstance(identity);
        consumer.accept(controller);
    }
}
