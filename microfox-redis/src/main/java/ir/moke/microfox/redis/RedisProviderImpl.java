package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.RedisConfig;
import ir.moke.microfox.api.redis.RedisProvider;
import org.redisson.api.RedissonClient;

public class RedisProviderImpl implements RedisProvider {

    @Override
    public void register(String identity, RedisConfig config) {
        RedisFactory.register(identity, config);
    }

    @Override
    public void unregister(String identity) {
        RedisFactory.unregister(identity);
    }

    @Override
    public RedissonClient client(String identity) {
        return RedisFactory.getClient(identity);
    }

}
