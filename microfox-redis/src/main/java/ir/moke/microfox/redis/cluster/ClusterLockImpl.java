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


    @Override
    public boolean tryLock(Duration waitTime, Duration leaseTime) {
        try {
            return lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void lock(Duration leaseTime) {
        lock.lock(leaseTime.toMillis(), TimeUnit.MILLISECONDS);
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
