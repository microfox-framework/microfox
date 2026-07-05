package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterLeaderElection;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ClusterLeaderElectionImpl implements ClusterLeaderElection {

    private final String name;
    private final RLock lock;
    private volatile boolean leader = false;

    public ClusterLeaderElectionImpl(String name, RedissonClient client) {
        this.name = name;
        this.lock = client.getLock(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean tryBecomeLeader(Duration leaseTime) {
        try {
            boolean acquired = lock.tryLock(0, leaseTime.toMillis(), TimeUnit.MILLISECONDS);
            leader = acquired;
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean isLeader() {
        return leader && lock.isHeldByCurrentThread();
    }

    @Override
    public void release() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
        leader = false;
    }

    @Override
    public void runIfLeader(Runnable task) {
        if (isLeader()) {
            task.run();
        }
    }
}
