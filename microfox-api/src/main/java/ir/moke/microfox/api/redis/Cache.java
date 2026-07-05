package ir.moke.microfox.api.redis;

import java.time.Duration;

public interface Cache {

    String get(String key);

    void set(String key, String value);

    void set(String key, String value, Duration ttl);

    boolean exists(String key);

    void delete(String key);

    void expire(String key, Duration ttl);
}
