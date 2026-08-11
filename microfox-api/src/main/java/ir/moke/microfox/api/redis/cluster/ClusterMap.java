package ir.moke.microfox.api.redis.cluster;

import java.util.Set;

public interface ClusterMap<K, V> {
    V get(K key);

    void put(K key, V value);

    V remove(K key);

    boolean containsKey(K key);

    Set<K> keys(String pattern);

    Set<K> keys();
}
