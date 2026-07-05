package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.Cache;
import ir.moke.microfox.api.redis.ClusterCoordinator;
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
    public Cache cache(String identity) {
        RedissonClient client = RedisFactory.getClient(identity);
        return new RedisCacheImpl(client);
    }

    @Override
    public ClusterCoordinator cluster(String identity) {
        RedissonClient client = RedisFactory.getClient(identity);
        return new ClusterCoordinatorImpl(client);
    }
}
