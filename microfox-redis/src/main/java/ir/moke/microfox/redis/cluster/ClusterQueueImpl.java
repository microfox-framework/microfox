package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

public class ClusterQueueImpl<E> implements ClusterQueue<E> {
    private final String name;
    private final RQueue<E> queue;

    public ClusterQueueImpl(String name, RedissonClient client) {
        this.name = name;
        this.queue = client.getQueue(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public void add(E element) {
        queue.add(element);
    }

    @Override
    public E poll() {
        return queue.poll();
    }

    @Override
    public E peek() {
        return queue.peek();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
