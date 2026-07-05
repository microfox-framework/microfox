package ir.moke.microfox.api.redis.cluster;

public interface ClusterLock {
    boolean tryLock(long waitTime, long leaseTime);

    void lock(long leaseTime);

    void lock();

    void unlock();

    boolean isLocked();

    boolean isHeldByCurrentThread();
}
