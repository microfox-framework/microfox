package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.RedisConfig;
import ir.moke.microfox.exception.MicroFoxException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RedisFactory {
    private static final Map<String, RedissonClient> CONFIG_MAP = new HashMap<>();

    public static void register(String identity, RedisConfig redisConfig) {
        if (identity == null) throw new MicroFoxException("Identity could not be null");
        if (redisConfig == null) throw new MicroFoxException("config could not be null");
        if (redisConfig.getHost() == null) throw new MicroFoxException("hostname could not be null");
        if (redisConfig.getPort() == null) throw new MicroFoxException("port could not be null");

        RedissonClient redissonClient = buildRedisClient(redisConfig);
        CONFIG_MAP.put(identity, redissonClient);
    }

    public static void unregister(String identity) {
        if (identity == null) throw new MicroFoxException("Identity could not be null");
        RedissonClient redissonClient = CONFIG_MAP.remove(identity);
        if (redissonClient != null) redissonClient.shutdown();
    }

    public static RedissonClient getClient(String identity) {
        return CONFIG_MAP.get(identity);
    }

    public static RedissonClient buildRedisClient(RedisConfig redisConfig) {
        try {
            Config config = new Config();
            SingleServerConfig singleServerConfig = config.useSingleServer();
            singleServerConfig.setAddress("redis://%s:%s".formatted(redisConfig.getHost(), redisConfig.getPort()));

            Optional.ofNullable(redisConfig.getMinIdle()).ifPresent(singleServerConfig::setConnectionMinimumIdleSize);
            Optional.ofNullable(redisConfig.getMaxTotal()).ifPresent(singleServerConfig::setConnectionPoolSize);
            Optional.ofNullable(redisConfig.getClientName()).ifPresent(singleServerConfig::setClientName);
            Optional.ofNullable(redisConfig.getDatabase()).ifPresent(singleServerConfig::setDatabase);
            Optional.ofNullable(redisConfig.getTimeout()).ifPresent(singleServerConfig::setTimeout);

            Optional.ofNullable(redisConfig.getUsername()).ifPresent(config::setUsername);
            Optional.ofNullable(redisConfig.getPassword()).ifPresent(config::setPassword);

            return Redisson.create(config);
        } catch (Exception e) {
            throw new MicroFoxException("Failed to create Redisson client", e);
        }
    }
}
