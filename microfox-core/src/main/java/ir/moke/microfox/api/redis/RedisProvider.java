package ir.moke.microfox.api.redis;

public interface RedisProvider {
    void register(String identity, RedisConfig config);
    void unregister(String identity);

    <T> T unwrap(String identity, Class<T> connectionClassType);
}
