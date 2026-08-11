package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterMap;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;

import java.util.Set;

public class ClusterMapImpl<V> implements ClusterMap<V> {
    private final String name;
    private final RMap<String, V> map;

    public ClusterMapImpl(String name, RedissonClient client) {
        this.name = name;
        this.map = client.getMap(name, new CompositeCodec(StringCodec.INSTANCE, client.getConfig().getCodec()));
    }

    public String getName() {
        return name;
    }

    @Override
    public V get(String key) {
        return map.get(key);
    }

    @Override
    public void put(String key, V value) {
        map.put(key, value);
    }

    @Override
    public V remove(String key) {
        return map.remove(key);
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public Set<String> keys(String pattern) {
        return map.keySet(pattern);
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }
}
