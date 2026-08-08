package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterLeaderElection;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ClusterLeaderElectionImpl implements ClusterLeaderElection {

    private final String name;
    private final RLock lock;

    public ClusterLeaderElectionImpl(String name, RedissonClient client) {
        this.name = name;
        this.lock = client.getLock(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean tryBecomeLeader() {
        try {
            return lock.tryLock(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean isLeader() {
        return lock.isHeldByCurrentThread();
    }

    @Override
    public void release() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Override
    public void runIfLeader(Runnable task) {
        if (isLeader()) {
            task.run();
        }
    }
}
