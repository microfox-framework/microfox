package test.redis;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.redis.RedisConfig;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import static ir.moke.microfox.MicroFox.redisRegister;

public class RedisTest {

    private static final RedisConfig config = new RedisConfig("172.17.0.1", 6379);
    private static final String IDENTITY = "redis-test";

    static {
        config.setPassword("adminpass");
        redisRegister(IDENTITY, config);
    }

    @Test
    public void save() {
        RedissonClient client = MicroFox.redis(IDENTITY);
        RBucket<Object> bucket = client.getBucket("name", StringCodec.INSTANCE);
        bucket.set("Ali");
        System.out.println(bucket.get());
    }
}
