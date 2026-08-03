package ir.moke.microfox.api.redis.cluster.stream;

import java.util.Map;

public interface StreamEntry<K, V> {
    String id();
    Map<K, V> fields();
}