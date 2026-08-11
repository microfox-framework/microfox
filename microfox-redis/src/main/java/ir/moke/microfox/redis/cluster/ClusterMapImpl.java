package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterMap;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClusterMapImpl<K, V> implements ClusterMap<K, V> {
    private final String name;
    private final RMap<K, V> map;

    public ClusterMapImpl(String name, RedissonClient client) {
        this.name = name;
        this.map = client.getMap(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public Set<K> keys(String pattern) {
        return map.keySet(pattern);
    }

    @Override
    public Set<K> keys() {
        return map.keySet();
    }
}
