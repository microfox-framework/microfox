package ir.moke.microfox.redis.cluster;

import ir.moke.microfox.api.redis.cluster.ClusterCountDownLatch;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ClusterCountDownLatchImpl implements ClusterCountDownLatch {

    private final String name;
    private final RCountDownLatch countDownLatch;

    public ClusterCountDownLatchImpl(String name, RedissonClient client) {
        this.name = name;
        this.countDownLatch = client.getCountDownLatch(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    @Override
    public boolean await(Duration timeout) {
        try {
            return countDownLatch.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void countDown() {
        countDownLatch.countDown();
    }

    @Override
    public long getCount() {
        return countDownLatch.getCount();
    }

    @Override
    public void trySetCount(long count) {
        countDownLatch.trySetCount(count);
    }
}
