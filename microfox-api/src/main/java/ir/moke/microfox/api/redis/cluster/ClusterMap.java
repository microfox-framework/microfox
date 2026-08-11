package ir.moke.microfox.api.redis.cluster;

import java.util.Set;

public interface ClusterMap<V> {
    V get(String key);

    void put(String key, V value);

    V remove(String key);

    boolean containsKey(String key);

    Set<String> keys(String pattern);

    Set<String> keys();
}
