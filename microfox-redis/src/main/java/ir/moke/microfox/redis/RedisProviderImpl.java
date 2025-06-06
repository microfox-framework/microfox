package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.RedisProvider;
import redis.clients.jedis.Jedis;

import java.util.function.Consumer;

public class RedisProviderImpl implements RedisProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> void redis(String identity, Consumer<T> consumer) {
        try (Jedis jedis = RedisFactory.getJedis(identity)) {
            consumer.accept((T) jedis);
        }
    }
}
