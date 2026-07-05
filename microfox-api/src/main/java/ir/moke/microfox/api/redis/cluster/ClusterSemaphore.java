package ir.moke.microfox.api.redis.cluster;

import java.time.Duration;

public interface ClusterSemaphore {
    boolean tryAcquire(int permits, Duration waitTime);

    void acquire(int permits);

    void release(int permits);

    int availablePermits();
}
