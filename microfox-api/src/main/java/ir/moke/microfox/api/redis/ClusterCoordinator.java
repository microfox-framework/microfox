package ir.moke.microfox.api.redis;

import ir.moke.microfox.api.redis.cluster.*;

public interface ClusterCoordinator {
    ClusterLock getLock(String name);

    ClusterTopic getTopic(String name);

    <K, V> ClusterMap<K, V> getMap(String name);

    <E> ClusterQueue<E> getQueue(String name);

    ClusterAtomicLong getAtomicLong(String name);

    ClusterSemaphore getSemaphore(String name);

    ClusterCountDownLatch getCountDownLatch(String name);

    ClusterLeaderElection leaderElection(String name);
}
