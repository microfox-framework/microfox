package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.RedisConfig;
import ir.moke.microfox.api.redis.RedisProvider;

public class RedisProviderImpl implements RedisProvider {

    @Override
    public void register(String identity, RedisConfig config) {
        RedisFactory.register(identity, config);
    }

    @Override
    public void unregister(String identity) {

    }

    @Override
    public <T> T unwrap(String identity, Class<T> connectionClassType) {
        return RedisFactory.buildRedisConnection(identity, connectionClassType);
    }
}
