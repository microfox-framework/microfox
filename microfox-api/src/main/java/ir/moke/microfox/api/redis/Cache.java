package ir.moke.microfox.api.redis;

import java.time.Duration;

public interface Cache {
    <T> T get(String key);

    void set(String key, Object value);

    void set(String key, Object value, Duration ttl);

    boolean exists(String key);

    void delete(String key);

    void expire(String key, Duration ttl);
}
