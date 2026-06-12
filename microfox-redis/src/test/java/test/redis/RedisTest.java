package test.redis;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.redis.RedisConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.RedisClient;

public class RedisTest {

    private static final RedisConfig config = new RedisConfig("127.0.0.1", 6379);

    static {
        MicroFox.redisRegister("sample", config);
    }

    @Test
    public void save() {
        try (RedisClient client = (RedisClient) MicroFox.redis("sample")) {
            client.set("name", "ali");
            Assertions.assertEquals("ali", client.get("name"));
        }
    }
}
