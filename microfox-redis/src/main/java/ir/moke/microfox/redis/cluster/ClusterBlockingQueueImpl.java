package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterBlockingQueue;
import ir.moke.microfox.exception.MicroFoxException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;

import java.util.concurrent.Future;

public class ClusterBlockingQueueImpl<E> implements ClusterBlockingQueue<E> {
    private final String name;
    private final RBlockingQueue<E> queue;

    public ClusterBlockingQueueImpl(String name, RedissonClient client) {
        this.name = name;
        this.queue = client.getBlockingQueue(name);
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
    public E take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new MicroFoxException(e);
        }
    }

    @Override
    public Future<E> takeAsync() {
        return queue.takeAsync();
    }

    @Override
    public E element() {
        return queue.element();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
