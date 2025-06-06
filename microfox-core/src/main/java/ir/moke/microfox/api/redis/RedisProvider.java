package ir.moke.microfox.api.redis;

import java.util.function.Consumer;

public interface RedisProvider {
    void redis(String identity, Consumer<Redis> consumer);
}
