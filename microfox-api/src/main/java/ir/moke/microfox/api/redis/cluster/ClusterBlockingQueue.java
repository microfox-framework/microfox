package ir.moke.microfox.api.redis.cluster;

import java.util.concurrent.Future;

public interface ClusterBlockingQueue<E> {
    void add(E element);

    E poll();

    E peek();

    E take();

    Future<E> takeAsync();

    E element();

    int size();
}
