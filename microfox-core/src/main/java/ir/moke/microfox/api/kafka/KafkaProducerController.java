package ir.moke.microfox.api.kafka;

import java.util.List;
import java.util.Map;

public interface KafkaProducerController<K, V> {
    void send(String topic, Integer partition, Long timestamp, K key, V value, Map<String, byte[]> headers);

    void batchSend(String topic, Integer partition, Long timestamp, List<K> key, List<V> value, Map<String, byte[]> headers);

    void txBegin();

    void txCommit();

    void txAbort();
}
