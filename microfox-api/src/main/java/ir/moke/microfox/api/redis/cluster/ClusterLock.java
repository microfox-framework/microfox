package ir.moke.microfox.api.redis.cluster;

import java.time.Duration;

public interface ClusterLock {
    boolean tryLock(Duration waitTime, Duration leaseTime);

    void lock(Duration leaseTime);

    void unlock();

    boolean isLocked();

    boolean isHeldByCurrentThread();
}
