package test.redis;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.redis.Cache;
import ir.moke.microfox.api.redis.RedisConfig;
import org.junit.jupiter.api.Test;

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
        Cache cache = MicroFox.redis(IDENTITY);

        cache.set("name", "Ali");
        System.out.println(cache.getKeysByPattern("n*"));
    }
}
