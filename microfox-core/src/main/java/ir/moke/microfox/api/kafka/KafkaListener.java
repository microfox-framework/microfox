package ir.moke.microfox.api.kafka;

import java.util.Map;

@FunctionalInterface
public interface KafkaListener<K, V> {
    void onMessage(String topic,
                   K key,
                   V value,
                   int partition,
                   long offset,
                   long timestamp,
                   int serializedKeySize,
                   int serializedValueSize,
                   Map<String, byte[]> headers,
                   Integer leaderEpoch,
                   Short deliveryCount);
}
