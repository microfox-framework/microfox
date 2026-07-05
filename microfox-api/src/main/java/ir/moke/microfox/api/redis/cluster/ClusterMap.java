package ir.moke.microfox.api.redis.cluster;

public interface ClusterMap<K, V> {
    V get(K key);

    void put(K key, V value);

    V remove(K key);

    boolean containsKey(K key);
}
