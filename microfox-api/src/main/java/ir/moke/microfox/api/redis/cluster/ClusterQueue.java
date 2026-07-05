package ir.moke.microfox.api.redis.cluster;

public interface ClusterQueue<E> {
    void add(E element);

    E poll();

    E peek();

    int size();
}
