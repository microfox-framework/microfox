package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.RedisConfig;
import ir.moke.microfox.exception.MicroFoxException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Connection;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.RedisClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RedisFactory {
    private static final Map<String, RedisClient> CONFIG_MAP = new HashMap<>();

    public static void register(String identity, RedisConfig config) {
        if (identity == null) throw new MicroFoxException("Identity could not be null");
        if (config == null) throw new MicroFoxException("config could not be null");
        if (config.getHost() == null) throw new MicroFoxException("hostname could not be null");
        if (config.getPort() == null) throw new MicroFoxException("port could not be null");

        RedisClient redisClient = buildRedisClient(config);
        CONFIG_MAP.put(identity, redisClient);
    }

    public static void unregister(String identity) {
        if (identity == null) throw new MicroFoxException("Identity could not be null");
        RedisClient redisClient = CONFIG_MAP.remove(identity);
        if (redisClient != null) redisClient.close();
    }

    public static RedisClient getClient(String identity) {
        return CONFIG_MAP.get(identity);
    }

    public static RedisClient buildRedisClient(RedisConfig config) {
        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        Optional.ofNullable(config.getMinIdle()).ifPresent(poolConfig::setMinIdle);
        Optional.ofNullable(config.getMaxIdle()).ifPresent(poolConfig::setMaxIdle);
        Optional.ofNullable(config.getMaxTotal()).ifPresent(poolConfig::setMaxTotal);
        Optional.ofNullable(config.isFireness()).ifPresent(poolConfig::setFairness);
        Optional.ofNullable(config.isLifo()).ifPresent(poolConfig::setLifo);
        Optional.ofNullable(config.getMaxWait()).ifPresent(poolConfig::setMaxWait);

        DefaultJedisClientConfig.Builder jedisClientBuilder = DefaultJedisClientConfig.builder();
        Optional.ofNullable(config.getClientName()).ifPresent(jedisClientBuilder::clientName);
        Optional.ofNullable(config.getUsername()).ifPresent(jedisClientBuilder::user);
        Optional.ofNullable(config.getPassword()).ifPresent(jedisClientBuilder::password);
        Optional.ofNullable(config.getDatabase()).ifPresent(jedisClientBuilder::database);
        Optional.ofNullable(config.getTimeout()).ifPresent(jedisClientBuilder::timeoutMillis);
        DefaultJedisClientConfig clientConfig = jedisClientBuilder.build();

        return RedisClient.builder()
                .hostAndPort(config.getHost(), config.getPort())
                .clientConfig(clientConfig)
                .poolConfig(poolConfig)
                .build();
    }
}
