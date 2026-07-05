package ir.moke.microfox.api.redis.cluster;

import java.time.Duration;

public interface ClusterCountDownLatch {
    void await() throws InterruptedException;

    boolean await(Duration timeout);

    void countDown();

    long getCount();

    void trySetCount(long count);
}
