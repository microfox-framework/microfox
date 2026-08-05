package ir.moke.microfox.api.redis.cluster.topic;

import java.util.EventListener;

@FunctionalInterface
public interface PatternMessageListener<M> extends EventListener {
    void onMessage(CharSequence pattern, CharSequence channel, M msg);
}

