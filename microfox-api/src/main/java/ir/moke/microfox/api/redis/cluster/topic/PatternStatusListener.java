package ir.moke.microfox.api.redis.cluster.topic;

import java.util.EventListener;

public interface PatternStatusListener extends EventListener {
    void onPSubscribe(String pattern);

    void onPUnsubscribe(String pattern);
}
