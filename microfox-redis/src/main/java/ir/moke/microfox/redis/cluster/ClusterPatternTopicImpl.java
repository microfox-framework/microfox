package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.topic.ClusterPatternTopic;
import ir.moke.microfox.api.redis.cluster.topic.PatternMessageListener;
import ir.moke.microfox.api.redis.cluster.topic.PatternStatusListener;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.Future;

public class ClusterPatternTopicImpl implements ClusterPatternTopic {
    private final String pattern;
    private final RPatternTopic topic;

    public ClusterPatternTopicImpl(String pattern, RedissonClient client) {
        this.pattern = pattern;
        this.topic = client.getPatternTopic(pattern);
    }

    @Override
    public List<String> getPatternNames() {
        return topic.getPatternNames();
    }

    @Override
    public <T> int addListener(Class<T> type, PatternMessageListener<T> listener) {
        return topic.addListener(type, listener::onMessage);
    }

    @Override
    public int addListener(PatternStatusListener listener) {
        return topic.addListener(new org.redisson.api.listener.PatternStatusListener() {
            @Override
            public void onPSubscribe(String pattern) {
                listener.onPSubscribe(pattern);
            }

            @Override
            public void onPUnsubscribe(String pattern) {
                listener.onPUnsubscribe(pattern);
            }
        });
    }

    @Override
    public void removeListener(Integer... ids) {
        topic.removeListener(ids);
    }

    @Override
    public <M> void removeListener(PatternMessageListener<? super M> listener) {
        topic.removeListener((org.redisson.api.listener.PatternMessageListener<M>) listener::onMessage);
    }

    @Override
    public void removeAllListeners() {
        topic.removeAllListeners();
    }

    @Override
    public void removeAllListenersAsync() {
        topic.removeAllListenersAsync();
    }

    @Override
    public Future<Integer> addListenerAsync(PatternStatusListener listener) {
        return topic.addListenerAsync(new org.redisson.api.listener.PatternStatusListener() {
            @Override
            public void onPSubscribe(String pattern) {
                listener.onPSubscribe(pattern);
            }

            @Override
            public void onPUnsubscribe(String pattern) {
                listener.onPUnsubscribe(pattern);
            }
        });
    }

    @Override
    public <T> Future<Integer> addListenerAsync(Class<T> type, PatternMessageListener<T> listener) {
        return topic.addListenerAsync(type, listener::onMessage);
    }

    @Override
    public void removeListenerAsync(Integer... ids) {
        topic.removeListenerAsync(ids);
    }

    @Override
    public Future<List<String>> getActiveTopicsAsync() {
        return topic.getActiveTopicsAsync();
    }

    @Override
    public List<String> getActiveTopics() {
        return topic.getActiveTopics();
    }
}
