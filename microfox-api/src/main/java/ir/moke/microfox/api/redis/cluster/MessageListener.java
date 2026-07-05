package ir.moke.microfox.api.redis.cluster;

public interface MessageListener<T> {
    void onMessage(T message);
}
