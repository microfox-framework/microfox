package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.ClusterCoordinator;
import ir.moke.microfox.api.redis.cluster.*;
import ir.moke.microfox.api.redis.cluster.stream.ClusterStream;
import ir.moke.microfox.api.redis.cluster.topic.ClusterPatternTopic;
import ir.moke.microfox.api.redis.cluster.topic.ClusterTopic;
import ir.moke.microfox.redis.cluster.*;
import org.redisson.api.RedissonClient;

public class ClusterCoordinatorImpl implements ClusterCoordinator {
    private final RedissonClient client;

    public ClusterCoordinatorImpl(RedissonClient client) {
        this.client = client;
    }

    @Override
    public ClusterLock getLock(String name) {
        return new ClusterLockImpl(name, client);
    }

    @Override
    public ClusterTopic getTopic(String name) {
        return new ClusterTopicImpl(name, client);
    }

    @Override
    public ClusterPatternTopic getPatternTopic(String pattern) {
        return new ClusterPatternTopicImpl(pattern, client);
    }

    @Override
    public <K, V> ClusterMap<K, V> getMap(String name) {
        return new ClusterMapImpl<>(name, client);
    }

    @Override
    public <E> ClusterQueue<E> getQueue(String name) {
        return new ClusterQueueImpl<>(name, client);
    }

    @Override
    public ClusterAtomicLong getAtomicLong(String name) {
        return new ClusterAtomicLongImpl(name, client);
    }

    @Override
    public ClusterSemaphore getSemaphore(String name) {
        return new ClusterSemaphoreImpl(name, client);
    }

    @Override
    public ClusterCountDownLatch getCountDownLatch(String name) {
        return new ClusterCountDownLatchImpl(name, client);
    }

    @Override
    public ClusterLeaderElection leaderElection(String name) {
        return new ClusterLeaderElectionImpl(name, client);
    }

    @Override
    public <K, V> ClusterStream<K, V> getStream(String name) {
        return null;
    }
}
