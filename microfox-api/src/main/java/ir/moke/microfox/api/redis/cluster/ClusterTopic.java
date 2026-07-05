package ir.moke.microfox.api.redis.cluster;

public interface ClusterTopic {
    void publish(Object message);

    <T> void subscribe(Class<T> type, MessageListener<T> listener);
}
