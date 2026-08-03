package ir.moke.microfox.api.redis.cluster.stream;

import java.util.List;
import java.util.Map;

public interface ClusterStream<K, V> {
    String add(Map<K, V> fields);

    String add(Map<K, V> fields, int maxLen);

    List<StreamEntry<K, V>> read(int count);

    List<StreamEntry<K, V>> read(int count, String lastId);

    List<StreamEntry<K, V>> range(String startId, String endId);

    void ack(String groupName, String... ids);

    void createGroup(String groupName);

    void createGroup(String groupName, String startId);

    List<StreamEntry<K, V>> readGroup(String groupName, String consumerName, int count);

    void trimToLength(int maxLen);
}
