package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterSemaphore;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ClusterSemaphoreImpl implements ClusterSemaphore {

    private final String name;
    private final RSemaphore semaphore;

    public ClusterSemaphoreImpl(String name, RedissonClient client) {
        this.name = name;
        this.semaphore = client.getSemaphore(name);
    }

    @Override
    public boolean tryAcquire(int permits, Duration waitTime) {
        try {
            return semaphore.tryAcquire(permits, waitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void acquire(int permits) {
        try {
            semaphore.acquire(permits);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void release(int permits) {
        semaphore.release(permits);
    }

    @Override
    public int availablePermits() {
        return semaphore.availablePermits();
    }
}
