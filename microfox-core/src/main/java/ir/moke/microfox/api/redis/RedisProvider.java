package ir.moke.microfox.api.redis;

import java.util.function.Consumer;

public interface RedisProvider {
    <T> void redis(String identity, Consumer<T> callback);

}
