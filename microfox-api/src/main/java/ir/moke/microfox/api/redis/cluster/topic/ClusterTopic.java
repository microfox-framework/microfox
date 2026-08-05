package ir.moke.microfox.api.redis.cluster.topic;

import java.util.List;
import java.util.concurrent.Future;

public interface ClusterTopic {
    List<String> getChannelNames();

    <M> int addListener(Class<M> type, MessageListener<? super M> listener);

    int addListener(StatusListener listener);

    <M> void removeListener(MessageListener<? super M> listener);

    void removeListener(Integer... listenerIds);

    void removeAllListeners();

    int countListeners();

    long countSubscribers();

    void addListenerAsync(StatusListener listener);

    <M> void addListenerAsync(Class<M> type, MessageListener<? super M> listener);

    void removeListenerAsync(Integer... listenerIds);

    <M> void removeListenerAsync(MessageListener<? super M> listener);

    Future<Long> countSubscribersAsync();

    void removeAllListenersAsync();

    void publish(Object message);

    void publishAsync(Object message);

    <T> void subscribe(Class<T> type, MessageListener<T> listener);

    <T> void subscribeAsync(Class<T> type, MessageListener<T> listener);
}
