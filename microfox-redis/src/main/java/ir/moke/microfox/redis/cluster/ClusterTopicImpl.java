package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.topic.ClusterTopic;
import ir.moke.microfox.api.redis.cluster.topic.MessageListener;
import ir.moke.microfox.api.redis.cluster.topic.StatusListener;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.Future;

public class ClusterTopicImpl implements ClusterTopic {

    private final String name;
    private final RTopic topic;

    public ClusterTopicImpl(String name, RedissonClient client) {
        this.name = name;
        this.topic = client.getTopic(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public List<String> getChannelNames() {
        return topic.getChannelNames();
    }

    @Override
    public <M> int addListener(Class<M> type, MessageListener<? super M> listener) {
        return topic.addListener(type, listener::onMessage);
    }

    @Override
    public int addListener(StatusListener listener) {
        return topic.addListener(new org.redisson.api.listener.StatusListener() {
            @Override
            public void onSubscribe(String channel) {
                listener.onSubscribe(channel);
            }

            @Override
            public void onUnsubscribe(String channel) {
                listener.onUnsubscribe(channel);
            }
        });
    }

    @Override
    public <M> void removeListener(MessageListener<? super M> listener) {
        topic.removeListener((org.redisson.api.listener.MessageListener<M>) listener::onMessage);
    }

    @Override
    public void removeListener(Integer... listenerIds) {
        topic.removeListener(listenerIds);
    }

    @Override
    public void removeAllListeners() {
        topic.removeAllListeners();
    }

    @Override
    public int countListeners() {
        return topic.countListeners();
    }

    @Override
    public long countSubscribers() {
        return topic.countSubscribers();
    }

    @Override
    public void addListenerAsync(StatusListener listener) {
        topic.addListenerAsync(new org.redisson.api.listener.StatusListener() {
            @Override
            public void onSubscribe(String channel) {
                listener.onSubscribe(channel);
            }

            @Override
            public void onUnsubscribe(String channel) {
                listener.onUnsubscribe(channel);
            }
        });
    }

    @Override
    public <M> void addListenerAsync(Class<M> type, MessageListener<? super M> listener) {
        topic.addListenerAsync(type, listener::onMessage);
    }

    @Override
    public void removeListenerAsync(Integer... listenerIds) {
        topic.removeListenerAsync(listenerIds);
    }

    @Override
    public <M> void removeListenerAsync(MessageListener<? super M> listener) {
        topic.removeListenerAsync();
    }

    @Override
    public Future<Long> countSubscribersAsync() {
        return topic.countSubscribersAsync();
    }

    @Override
    public void removeAllListenersAsync() {
        topic.removeAllListenersAsync();
    }

    @Override
    public void publish(Object message) {
        topic.publish(message);
    }

    @Override
    public void publishAsync(Object message) {
        topic.publishAsync(message);
    }

    @Override
    public <T> void subscribe(Class<T> type, MessageListener<T> listener) {
        topic.addListener(type, listener::onMessage);
    }

    @Override
    public <T> void subscribeAsync(Class<T> type, MessageListener<T> listener) {
        topic.addListenerAsync(type, listener::onMessage);
    }
}
