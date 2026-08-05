package ir.moke.microfox.api.redis.cluster.topic;

import java.util.EventListener;

@FunctionalInterface
public interface MessageListener<M> extends EventListener {
    void onMessage(CharSequence channel, M msg);
}
