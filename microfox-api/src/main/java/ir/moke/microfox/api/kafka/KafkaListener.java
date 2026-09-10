package ir.moke.microfox.api.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

@FunctionalInterface
public interface KafkaListener<K, V> {
    void onMessage(ConsumerRecord<K, V> record);
}
