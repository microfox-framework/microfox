package ir.moke.microfox.api.redis;

import ir.moke.microfox.api.redis.cluster.*;
import ir.moke.microfox.api.redis.cluster.stream.ClusterStream;
import ir.moke.microfox.api.redis.cluster.topic.ClusterPatternTopic;
import ir.moke.microfox.api.redis.cluster.topic.ClusterTopic;

public interface ClusterCoordinator {
    ClusterLock getLock(String name);

    ClusterTopic getTopic(String name);

    ClusterPatternTopic getPatternTopic(String pattern);

    <V> ClusterMap<V> getMap(String name);

    <E> ClusterQueue<E> getQueue(String name);

    <E> ClusterBlockingQueue<E> getBlockingQueue(String name);

    ClusterAtomicLong getAtomicLong(String name);

    ClusterSemaphore getSemaphore(String name);

    ClusterCountDownLatch getCountDownLatch(String name);

    ClusterLeaderElection leaderElection(String name);

    <K, V> ClusterStream<K, V> getStream(String name);
}
