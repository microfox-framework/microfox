package ir.moke.microfox.api.redis.cluster;

@FunctionalInterface
public interface MessageListener<T> {
    void onMessage(T message);
}
