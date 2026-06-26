package ir.moke.microfox.kafka;

import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.api.kafka.KafkaProvider;
import ir.moke.microfox.api.kafka.KafkaStreamController;
import org.apache.kafka.streams.Topology;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

public class KafkaProviderImpl implements KafkaProvider {

    @Override
    public void registerProducer(String identity, Map<String, Object> config) {
        KafkaProducerFactory.register(identity, config);
    }

    @Override
    public void registerConsumer(String identity, Map<String, Object> config) {
        KafkaConsumerFactory.register(identity, config);
    }

    @Override
    public void registerStream(String identity, Map<String, Object> config) {
        KafkaStreamFactory.register(identity, config);
    }

    @Override
    public void unregisterProducer(String identity) {
        KafkaProducerFactory.close(identity, Duration.ZERO);
    }

    @Override
    public void unregisterConsumer(String identity) {
        KafkaConsumerFactory.close(identity, Duration.ZERO);

    }

    @Override
    public void unregisterStream(String identity) {
        KafkaStreamFactory.close(identity);
    }

    @Override
    public <K, V> void produce(String clientId, Consumer<KafkaProducerController<K, V>> consumer) {
        KafkaProducerController<K, V> controller = KafkaProducerFactory.createProxyInstance(clientId);
        consumer.accept(controller);
    }

    @Override
    public <K, V> void consumer(String clientId, Consumer<KafkaConsumerController<K, V>> consumer) {
        KafkaConsumerController<K, V> controller = KafkaConsumerFactory.createProxyInstance(clientId);
        consumer.accept(controller);
    }

    @Override
    public void stream(String clientId, Object topology, Consumer<KafkaStreamController> consumer) {
        KafkaStreamController kafkaStreamController = KafkaStreamFactory.createProxyInstance(clientId, (Topology) topology);
        consumer.accept(kafkaStreamController);
    }
}
