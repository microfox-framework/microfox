package ir.moke.microfox.api.redis;

import java.util.List;

public interface RedisTransaction {

    void multi();

    String watch(String... keys);

    String watch(byte[]... keys);

    String unwatch();

    void close();

    List<Object> exec();

    String discard();

}
