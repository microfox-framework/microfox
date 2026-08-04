package ir.moke.microfox.api.redis.cluster.topic;

import java.util.List;
import java.util.concurrent.Future;

public interface ClusterPatternTopic {
    List<String> getPatternNames();

    <T> int addListener(Class<T> type, PatternMessageListener<T> listener);

    int addListener(PatternStatusListener listener);

    void removeListener(Integer... ids);

    <M> void removeListener(PatternMessageListener<? super M> listener);

    void removeAllListeners();

    void removeAllListenersAsync();

    Future<Integer> addListenerAsync(PatternStatusListener listener);

    <T> Future<Integer> addListenerAsync(Class<T> type, PatternMessageListener<T> listener);

    void removeListenerAsync(Integer... ids);

    Future<List<String>> getActiveTopicsAsync();

    List<String> getActiveTopics();
}
