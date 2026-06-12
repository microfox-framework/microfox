import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.redis.RedisConfig;

public class RedisTest {
    static void main() {
        RedisConfig config = new RedisConfig("127.0.0.1", 6379);
        MicroFox.redisRegister("sample", config);


        StatefulRedisPubSubConnection<String, String> connection = MicroFox.redis("sample", StatefulRedisPubSubConnection.class);
        connection.sync().set("name", "ali");

        while (true) ;
    }
}
