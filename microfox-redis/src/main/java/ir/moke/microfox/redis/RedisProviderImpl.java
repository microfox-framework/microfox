package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.Redis;
import ir.moke.microfox.api.redis.RedisProvider;
import redis.clients.jedis.Jedis;

import java.util.function.Consumer;

public class RedisProviderImpl implements RedisProvider {
    @Override
    public void redis(String identity, Consumer<Redis> consumer) {
        try (Jedis jedis = RedisFactory.getJedis(identity)) {
            consumer.accept(new RedisImpl(jedis));
        }
    }
}
