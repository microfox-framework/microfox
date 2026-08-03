package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.stream.StreamEntry;

import java.util.Map;

public record StreamEntryImpl<K, V>(String id, Map<K, V> fields) implements StreamEntry<K, V> {
}
