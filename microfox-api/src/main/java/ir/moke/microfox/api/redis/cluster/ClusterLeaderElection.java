package ir.moke.microfox.api.redis.cluster;

public interface ClusterLeaderElection {
    boolean tryBecomeLeader();

    boolean isLeader();

    void release();

    void runIfLeader(Runnable task);
}
