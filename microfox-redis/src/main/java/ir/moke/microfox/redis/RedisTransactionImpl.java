package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.RedisTransaction;
import redis.clients.jedis.Transaction;

import java.util.List;

public class RedisTransactionImpl implements RedisTransaction {
    private final Transaction transaction;

    public RedisTransactionImpl(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void multi() {
        transaction.multi();
    }

    @Override
    public String watch(String... keys) {
        return transaction.watch(keys);
    }

    @Override
    public String watch(byte[]... keys) {
        return transaction.watch(keys);
    }

    @Override
    public String unwatch() {
        return transaction.unwatch();
    }

    @Override
    public void close() {
        transaction.close();
    }

    @Override
    public List<Object> exec() {
        return transaction.exec();
    }

    @Override
    public String discard() {
        return transaction.discard();
    }
}
