package ir.moke.microfox.api.kafka;

import java.util.List;
import java.util.Map;

public interface KafkaProducerController<K, V> {
    void send(String topic, K key, V value, Integer partition, Long timestamp, Map<String, byte[]> headers);

    default void send(String topic, K key, V value, Integer partition, Long timestamp) {
        send(topic, key, value, partition, timestamp, null);
    }

    default void send(String topic, K key, V value, Integer partition, Map<String, byte[]> headers) {
        send(topic, key, value, partition, null, null);
    }

    default void send(String topic, K key, V value, Integer partition) {
        send(topic, key, value, partition, null, null);
    }

    default void send(String topic, K key, V value) {
        send(topic, key, value, null, null, null);
    }

    default void send(String topic, V value) {
        send(topic, null, value, null, null, null);
    }

    void batchSend(String topic, Integer partition, Long timestamp, List<K> key, List<V> value, Map<String, byte[]> headers);

    void txBegin();

    void txCommit();

    void txAbort();
}
