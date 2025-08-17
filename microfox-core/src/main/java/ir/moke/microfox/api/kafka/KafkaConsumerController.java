package ir.moke.microfox.api.kafka;

import java.util.Collection;

public interface KafkaConsumerController<K, V> {

    void listen(Collection<String> topics, KafkaListener<K, V> listener);

    void pause();

    void resume();

    void close();

    void shutdown();
}