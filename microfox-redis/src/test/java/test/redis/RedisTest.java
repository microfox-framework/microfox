package test.redis;

import ir.moke.microfox.MicrofoxRegistry;
import ir.moke.microfox.api.redis.RedisConfig;
import org.junit.jupiter.api.Test;

public class RedisTest {

    private static final RedisConfig config = new RedisConfig("127.0.0.1", 6379);

    static {
        MicrofoxRegistry.redisRegister("sample", config);
    }

    @Test
    public void save() {

    }
}
