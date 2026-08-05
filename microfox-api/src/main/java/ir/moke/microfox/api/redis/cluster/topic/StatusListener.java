package ir.moke.microfox.api.redis.cluster.topic;

import java.util.EventListener;

public interface StatusListener extends EventListener {
    void onSubscribe(String channel);

    void onUnsubscribe(String channel);
}

