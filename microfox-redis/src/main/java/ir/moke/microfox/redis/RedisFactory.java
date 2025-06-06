package ir.moke.microfox.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisFactory {
    private static final Map<String, Jedis> jedisMap = new ConcurrentHashMap<>();
    private static final Jedis jedis = new Jedis();

    public static void register(String identity, JedisPoolConfig poolConfig, String host, int port, boolean ssl) {
        JedisPool jedisPool = new JedisPool(poolConfig, host, port, ssl);
        jedisMap.put(identity, jedisPool.getResource());
    }

    public static void register(String identity, String host, int port, boolean ssl) {
        Jedis jedis = new Jedis(host, port, ssl);
        jedisMap.put(identity, jedis);
    }

    public static Jedis getJedis(String identity) {
        return jedisMap.get(identity);
    }
}
