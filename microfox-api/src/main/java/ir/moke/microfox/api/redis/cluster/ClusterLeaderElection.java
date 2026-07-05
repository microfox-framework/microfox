package ir.moke.microfox.api.redis.cluster;

import java.time.Duration;

public interface ClusterLeaderElection {
    boolean tryBecomeLeader(Duration leaseTime);

    boolean isLeader();

    void release();

    void runIfLeader(Runnable task);
}
