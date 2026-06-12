package ir.moke.microfox.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.Delay;
import ir.moke.microfox.api.redis.RedisConfig;
import ir.moke.microfox.exception.MicroFoxException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RedisFactory {
    private static final Map<String, RedisClient> CONFIG_MAP = new HashMap<>();

    public static void register(String identity, RedisConfig config) {
        if (identity == null) throw new MicroFoxException("Identity could not be null");
        if (config == null) throw new MicroFoxException("config could not be null");
        if (config.host() == null) throw new MicroFoxException("hostname could not be null");
        if (config.port() == null) throw new MicroFoxException("port could not be null");

        RedisClient redisClient = buildRedisClient(config);
        CONFIG_MAP.put(identity, redisClient);
    }

    public static void unregister(String identity) {
        if (identity == null) throw new MicroFoxException("Identity could not be null");
        RedisClient redisClient = CONFIG_MAP.remove(identity);
        if (redisClient != null) redisClient.close();
    }

    @SuppressWarnings("unchecked")
    public static <T> T buildRedisConnection(String identity, Class<T> connectionType) {
        RedisClient client = getClient(identity);
        if (connectionType.isAssignableFrom(StatefulRedisConnection.class)) {
            return (T) client.connect();
        } else if (connectionType.isAssignableFrom(StatefulRedisPubSubConnection.class)) {
            return (T) client.connectPubSub();
        } else {
            throw new MicroFoxException("Connection type %s not supported yet".formatted(connectionType.getSimpleName()));
        }
    }

    public static RedisClient getClient(String identity) {
        return CONFIG_MAP.get(identity);
    }

    public static RedisClient buildRedisClient(RedisConfig config) {
        // Create Resource
        ClientResources.Builder resourceBuilder = ClientResources.builder();
        Optional.ofNullable(config.ioThreadPool()).ifPresent(resourceBuilder::ioThreadPoolSize);
        Optional.ofNullable(config.computationThreadPool()).ifPresent(resourceBuilder::computationThreadPoolSize);
        Optional.ofNullable(config.reconnectDelay()).ifPresent(item -> resourceBuilder.reconnectDelay(Delay.constant(Duration.ofSeconds(item))));
        ClientResources clientResources = resourceBuilder.build();

        // Create URI
        RedisURI.Builder uriBuilder = RedisURI.builder();
        Optional.of(config.host()).ifPresent(uriBuilder::withHost);
        Optional.of(config.port()).ifPresent(uriBuilder::withPort);
        Optional.ofNullable(config.clientName()).ifPresent(uriBuilder::withClientName);
        Optional.ofNullable(config.database()).ifPresent(uriBuilder::withDatabase);
        Optional.ofNullable(config.timeout()).ifPresent(item -> uriBuilder.withTimeout(Duration.ofSeconds(item)));
        Optional.ofNullable(config.ssl()).ifPresent(uriBuilder::withSsl);

        if (config.username() != null) {
            uriBuilder.withAuthentication(config.username(), config.password());
        } else if (config.password() != null) {
            uriBuilder.withPassword(config.password());
        }
        RedisURI redisURI = uriBuilder.build();

        // Create Client
        return RedisClient.create(clientResources, redisURI);
    }
}
