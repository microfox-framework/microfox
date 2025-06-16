package ir.moke.microfox.redis;

import ir.moke.microfox.exception.MicrofoxException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisFactory {
    private static final Map<String, Jedis> JEDIS_MAP = new ConcurrentHashMap<>();
    private static final Jedis jedis = new Jedis();

    public static void register(String identity, JedisPoolConfig poolConfig, String host, int port, boolean ssl) {
        if (JEDIS_MAP.containsKey(identity)) throw new MicrofoxException("Redis with identity %s already registered".formatted(identity));
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, ssl);
        JEDIS_MAP.put(identity, jedisPool.getResource());
    }

    public static void register(String identity, String host, int port, boolean ssl) {
        Jedis jedis = new Jedis(host, port, ssl);
        JEDIS_MAP.put(identity, jedis);
    }

    public static Jedis getJedis(String identity) {
        return JEDIS_MAP.get(identity);
    }
}
