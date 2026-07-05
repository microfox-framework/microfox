package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterTopic;
import ir.moke.microfox.api.redis.cluster.MessageListener;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

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
    public void publish(Object message) {
        topic.publish(message);
    }

    @Override
    public <T> void subscribe(Class<T> type, MessageListener<T> listener) {
        topic.addListener(type, (channel, msg) -> listener.onMessage(msg));
    }
}
