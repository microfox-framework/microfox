package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterAtomicLong;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

public class ClusterAtomicLongImpl implements ClusterAtomicLong {

    private final String name;
    private final RAtomicLong atomicLong;

    public ClusterAtomicLongImpl(String name, RedissonClient client) {
        this.name = name;
        this.atomicLong = client.getAtomicLong(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public long get() {
        return atomicLong.get();
    }

    @Override
    public void set(long value) {
        atomicLong.set(value);
    }

    @Override
    public long incrementAndGet() {
        return atomicLong.incrementAndGet();
    }

    @Override
    public long addAndGet(long value) {
        return atomicLong.addAndGet(value);
    }
}
