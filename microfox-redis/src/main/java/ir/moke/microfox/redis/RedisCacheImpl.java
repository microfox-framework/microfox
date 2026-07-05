package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.Cache;
import org.redisson.api.RedissonClient;

import java.time.Duration;

public class RedisCacheImpl implements Cache {
    private final RedissonClient client;

    public RedisCacheImpl(RedissonClient client) {
        this.client = client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) client.getBucket(key).get();
    }

    @Override
    public void set(String key, Object value) {
        client.getBucket(key).set(value);
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        client.getBucket(key).set(value, ttl);
    }

    @Override
    public boolean exists(String key) {
        return client.getBucket(key).isExists();
    }

    @Override
    public void delete(String key) {
        client.getBucket(key).delete();
    }

    @Override
    public void expire(String key, Duration ttl) {
        client.getBucket(key).expire(ttl);
    }
}
