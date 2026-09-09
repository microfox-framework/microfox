package ir.moke.microfox.api.redis;

import org.redisson.api.RedissonClient;

public interface RedisProvider {
    void register(String identity, RedisConfig config);

    void unregister(String identity);

    RedissonClient client(String identity);
}
