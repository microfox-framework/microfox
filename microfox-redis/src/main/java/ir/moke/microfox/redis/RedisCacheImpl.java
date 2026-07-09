package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.Cache;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.redisson.client.codec.StringCodec;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RedisCacheImpl implements Cache {
    private final RedissonClient client;

    public RedisCacheImpl(RedissonClient client) {
        this.client = client;
    }

    @Override
    public String get(String key) {
        try {
            return client.getBucket(key, StringCodec.INSTANCE).get().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void set(String key, String value) {
        client.getBucket(key, StringCodec.INSTANCE).set(value);
    }

    @Override
    public void set(String key, String value, Duration ttl) {
        client.getBucket(key, StringCodec.INSTANCE).set(value, ttl);
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

    public List<String> getKeysByPattern(String pattern) {
        List<String> result = new ArrayList<>();
        for (String key : client.getKeys().getKeys(KeysScanOptions.defaults().pattern(pattern))) {
            result.add(key);
        }
        return result;
    }
}
