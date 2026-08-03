package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.stream.ClusterStream;
import ir.moke.microfox.api.redis.cluster.stream.StreamEntry;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.stream.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClusterStreamImpl<K, V> implements ClusterStream<K, V> {

    private final RStream<K, V> stream;

    public ClusterStreamImpl(String name, RedissonClient client) {
        this.stream = client.getStream(name);
    }

    @Override
    public String add(Map<K, V> fields) {
        return stream.add(StreamAddArgs.entries(fields)).toString();
    }

    @Override
    public String add(Map<K, V> fields, int maxLen) {
        return stream.add(StreamAddArgs.entries(fields)).toString();
    }

    @Override
    public List<StreamEntry<K, V>> read(int count) {
        return toEntries(stream.read(StreamReadArgs.greaterThan(StreamMessageId.ALL).count(count)));
    }

    @Override
    public List<StreamEntry<K, V>> read(int count, String lastId) {
        return toEntries(stream.read(StreamReadArgs.greaterThan(toId(lastId)).count(count)));
    }

    @Override
    public List<StreamEntry<K, V>> range(String startId, String endId) {
        return toEntries(stream.range(StreamRangeArgs.startId(toId(startId)).endId(toId(endId))));
    }

    @Override
    public void ack(String groupName, String... ids) {
        stream.ack(groupName, Arrays.stream(ids).map(this::toId).toArray(StreamMessageId[]::new));
    }

    @Override
    public void createGroup(String groupName) {
        stream.createGroup(StreamCreateGroupArgs.name(groupName));
    }

    @Override
    public void createGroup(String groupName, String startId) {
        stream.createGroup(StreamCreateGroupArgs.name(groupName).id(toId(startId)));
    }

    @Override
    public List<StreamEntry<K, V>> readGroup(String groupName, String consumerName, int count) {
        return toEntries(stream.readGroup(groupName, consumerName, StreamReadGroupArgs.neverDelivered().count(count)));
    }

    @Override
    public void trimToLength(int maxLen) {
        stream.trim(StreamTrimArgs.maxLen(maxLen).noLimit());
    }

    private StreamMessageId toId(String id) {
        String[] parts = id.split("-");
        return parts.length == 2
                ? new StreamMessageId(Long.parseLong(parts[0]), Long.parseLong(parts[1]))
                : new StreamMessageId(Long.parseLong(parts[0]));
    }

    private List<StreamEntry<K, V>> toEntries(Map<StreamMessageId, Map<K, V>> result) {
        if (result == null) return Collections.emptyList();
        return result.entrySet().stream()
                .map(e -> new StreamEntryImpl<>(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());
    }
}
