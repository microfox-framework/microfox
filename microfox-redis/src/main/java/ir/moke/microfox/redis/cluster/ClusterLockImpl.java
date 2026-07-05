package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ClusterLockImpl implements ClusterLock {

    private final String name;
    private final RLock lock;

    public ClusterLockImpl(String name, RedissonClient client) {
        this.name = name;
        this.lock = client.getLock(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean tryLock(long waitTime, long leaseTime) {
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void lock(long leaseTime) {
        lock.lock(leaseTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Override
    public boolean isLocked() {
        return lock.isLocked();
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return lock.isHeldByCurrentThread();
    }
}
