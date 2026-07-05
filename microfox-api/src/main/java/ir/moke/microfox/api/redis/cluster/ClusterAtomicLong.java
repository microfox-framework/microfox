package ir.moke.microfox.api.redis.cluster;

public interface ClusterAtomicLong {
    long get();

    void set(long value);

    long incrementAndGet();

    long addAndGet(long value);
}
