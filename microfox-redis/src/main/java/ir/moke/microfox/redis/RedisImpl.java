package ir.moke.microfox.redis;

import ir.moke.microfox.api.redis.Redis;
import ir.moke.microfox.api.redis.RedisTransaction;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisImpl implements Redis {

    private final Jedis jedis;

    public RedisImpl(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public String toString() {
        return jedis.toString();
    }

    @Override
    public void connect() {
        jedis.connect();
    }

    @Override
    public void disconnect() {
        jedis.disconnect();
    }

    @Override
    public boolean isConnected() {
        return jedis.isConnected();
    }

    @Override
    public boolean isBroken() {
        return jedis.isBroken();
    }

    @Override
    public void resetState() {
        jedis.resetState();
    }

    @Override
    public void close() {
        jedis.close();
    }

    @Override
    public RedisTransaction multi() {
        return new RedisTransactionImpl(jedis.multi());
    }

    @Override
    public int getDB() {
        return jedis.getDB();
    }

    @Override
    public String ping() {
        return jedis.ping();
    }

    @Override
    public byte[] ping(byte[] message) {
        return jedis.ping(message);
    }

    @Override
    public String select(int index) {
        return jedis.select(index);
    }

    @Override
    public String swapDB(int index1, int index2) {
        return jedis.swapDB(index1, index2);
    }

    @Override
    public String flushDB() {
        return jedis.flushDB();
    }

    @Override
    public String flushAll() {
        return jedis.flushAll();
    }

    @Override
    public boolean copy(byte[] srcKey, byte[] dstKey, int db, boolean replace) {
        return jedis.copy(srcKey, dstKey, db, replace);
    }

    @Override
    public boolean copy(byte[] srcKey, byte[] dstKey, boolean replace) {
        return jedis.copy(srcKey, dstKey, replace);
    }

    @Override
    public String set(byte[] key, byte[] value) {
        return jedis.set(key, value);
    }

    @Override
    public byte[] get(byte[] key) {
        return jedis.get(key);
    }

    @Override
    public byte[] setGet(byte[] key, byte[] value) {
        return jedis.setGet(key, value);
    }

    @Override
    public byte[] getDel(byte[] key) {
        return jedis.getDel(key);
    }

    @Override
    public long exists(byte[]... keys) {
        return jedis.exists(keys);
    }

    @Override
    public boolean exists(byte[] key) {
        return jedis.exists(key);
    }

    @Override
    public long del(byte[]... keys) {
        return jedis.del(keys);
    }

    @Override
    public long del(byte[] key) {
        return jedis.del(key);
    }

    @Override
    public long unlink(byte[]... keys) {
        return jedis.unlink(keys);
    }

    @Override
    public long unlink(byte[] key) {
        return jedis.unlink(key);
    }

    @Override
    public String type(byte[] key) {
        return jedis.type(key);
    }

    @Override
    public Set<byte[]> keys(byte[] pattern) {
        return jedis.keys(pattern);
    }

    @Override
    public byte[] randomBinaryKey() {
        return jedis.randomBinaryKey();
    }

    @Override
    public String rename(byte[] oldkey, byte[] newkey) {
        return jedis.rename(oldkey, newkey);
    }

    @Override
    public long renamenx(byte[] oldkey, byte[] newkey) {
        return jedis.renamenx(oldkey, newkey);
    }

    @Override
    public long dbSize() {
        return jedis.dbSize();
    }

    @Override
    public long expire(byte[] key, long seconds) {
        return jedis.expire(key, seconds);
    }

    @Override
    public long pexpire(byte[] key, long milliseconds) {
        return jedis.expire(key, milliseconds);
    }

    @Override
    public long expireTime(byte[] key) {
        return jedis.expireTime(key);
    }

    @Override
    public long pexpireTime(byte[] key) {
        return jedis.pexpireTime(key);
    }

    @Override
    public long expireAt(byte[] key, long unixTime) {
        return jedis.expireAt(key, unixTime);
    }

    @Override
    public long pexpireAt(byte[] key, long millisecondsTimestamp) {
        return jedis.pexpireAt(key, millisecondsTimestamp);
    }

    @Override
    public long ttl(byte[] key) {
        return jedis.ttl(key);
    }

    @Override
    public long touch(byte[]... keys) {
        return jedis.touch(keys);
    }

    @Override
    public long touch(byte[] key) {
        return jedis.touch(key);
    }

    @Override
    public long move(byte[] key, int dbIndex) {
        return jedis.move(key, dbIndex);
    }

    @Override
    public List<byte[]> mget(byte[]... keys) {
        return jedis.mget(keys);
    }

    @Override
    public long setnx(byte[] key, byte[] value) {
        return jedis.setnx(key, value);
    }

    @Override
    public String setex(byte[] key, long seconds, byte[] value) {
        return jedis.setex(key, seconds, value);
    }

    @Override
    public String mset(byte[]... keysvalues) {
        return jedis.mset(keysvalues);
    }

    @Override
    public long msetnx(byte[]... keysvalues) {
        return jedis.msetnx(keysvalues);
    }

    @Override
    public long decrBy(byte[] key, long decrement) {
        return jedis.decrBy(key, decrement);
    }

    @Override
    public long decr(byte[] key) {
        return jedis.decr(key);
    }

    @Override
    public long incrBy(byte[] key, long increment) {
        return jedis.incrBy(key, increment);
    }

    @Override
    public double incrByFloat(byte[] key, double increment) {
        return jedis.incrByFloat(key, increment);
    }

    @Override
    public long incr(byte[] key) {
        return jedis.incr(key);
    }

    @Override
    public long append(byte[] key, byte[] value) {
        return jedis.append(key, value);
    }

    @Override
    public byte[] substr(byte[] key, int start, int end) {
        return jedis.substr(key, start, end);
    }

    @Override
    public long hset(byte[] key, byte[] field, byte[] value) {
        return jedis.hset(key, field, value);
    }

    @Override
    public long hset(byte[] key, Map<byte[], byte[]> hash) {
        return jedis.hset(key, hash);
    }

    @Override
    public byte[] hget(byte[] key, byte[] field) {
        return jedis.hget(key, field);
    }

    @Override
    public List<byte[]> hgetdel(byte[] key, byte[]... fields) {
        return jedis.hgetdel(key, fields);
    }

    @Override
    public long hsetnx(byte[] key, byte[] field, byte[] value) {
        return jedis.hsetnx(key, field, value);
    }

    @Override
    public String hmset(byte[] key, Map<byte[], byte[]> hash) {
        return jedis.hmset(key, hash);
    }

    @Override
    public List<byte[]> hmget(byte[] key, byte[]... fields) {
        return jedis.hmget(key, fields);
    }

    @Override
    public long hincrBy(byte[] key, byte[] field, long value) {
        return jedis.hincrBy(key, field, value);
    }

    @Override
    public double hincrByFloat(byte[] key, byte[] field, double value) {
        return jedis.hincrByFloat(key, field, value);
    }

    @Override
    public boolean hexists(byte[] key, byte[] field) {
        return jedis.hexists(key, field);
    }

    @Override
    public long hdel(byte[] key, byte[]... fields) {
        return jedis.hdel(key, fields);
    }

    @Override
    public long hlen(byte[] key) {
        return jedis.hlen(key);
    }

    @Override
    public Set<byte[]> hkeys(byte[] key) {
        return jedis.hkeys(key);
    }

    @Override
    public List<byte[]> hvals(byte[] key) {
        return jedis.hvals(key);
    }

    @Override
    public Map<byte[], byte[]> hgetAll(byte[] key) {
        return jedis.hgetAll(key);
    }

    @Override
    public byte[] hrandfield(byte[] key) {
        return jedis.hrandfield(key);
    }

    @Override
    public List<byte[]> hrandfield(byte[] key, long count) {
        return jedis.hrandfield(key, count);
    }

    @Override
    public List<Map.Entry<byte[], byte[]>> hrandfieldWithValues(byte[] key, long count) {
        return jedis.hrandfieldWithValues(key, count);
    }

    @Override
    public long rpush(byte[] key, byte[]... strings) {
        return jedis.rpush(key, strings);
    }

    @Override
    public long lpush(byte[] key, byte[]... strings) {
        return jedis.lpush(key, strings);
    }

    @Override
    public long llen(byte[] key) {
        return jedis.llen(key);
    }

    @Override
    public List<byte[]> lrange(byte[] key, long start, long stop) {
        return jedis.lrange(key, start, stop);
    }

    @Override
    public String ltrim(byte[] key, long start, long stop) {
        return jedis.ltrim(key, start, stop);
    }

    @Override
    public byte[] lindex(byte[] key, long index) {
        return jedis.lindex(key, index);
    }

    @Override
    public String lset(byte[] key, long index, byte[] value) {
        return jedis.lset(key, index, value);
    }

    @Override
    public long lrem(byte[] key, long count, byte[] value) {
        return jedis.lrem(key, count, value);
    }

    @Override
    public byte[] lpop(byte[] key) {
        return jedis.lpop(key);
    }

    @Override
    public List<byte[]> lpop(byte[] key, int count) {
        return jedis.lpop(key, count);
    }

    @Override
    public Long lpos(byte[] key, byte[] element) {
        return jedis.lpos(key, element);
    }

    @Override
    public byte[] rpop(byte[] key) {
        return jedis.rpop(key);
    }

    @Override
    public List<byte[]> rpop(byte[] key, int count) {
        return jedis.rpop(key, count);
    }

    @Override
    public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
        return jedis.rpoplpush(srckey, dstkey);
    }

    @Override
    public long sadd(byte[] key, byte[]... members) {
        return jedis.sadd(key, members);
    }

    @Override
    public Set<byte[]> smembers(byte[] key) {
        return jedis.smembers(key);
    }

    @Override
    public long srem(byte[] key, byte[]... members) {
        return jedis.srem(key, members);
    }

    @Override
    public byte[] spop(byte[] key) {
        return jedis.spop(key);
    }

    @Override
    public Set<byte[]> spop(byte[] key, long count) {
        return jedis.spop(key, count);
    }

    @Override
    public long smove(byte[] srckey, byte[] dstkey, byte[] member) {
        return jedis.smove(srckey, dstkey, member);
    }

    @Override
    public long scard(byte[] key) {
        return jedis.scard(key);
    }

    @Override
    public boolean sismember(byte[] key, byte[] member) {
        return jedis.sismember(key, member);
    }

    @Override
    public List<Boolean> smismember(byte[] key, byte[]... members) {
        return jedis.smismember(key, members);
    }

    @Override
    public Set<byte[]> sinter(byte[]... keys) {
        return jedis.sinter(keys);
    }

    @Override
    public long sinterstore(byte[] dstkey, byte[]... keys) {
        return jedis.sinterstore(dstkey, keys);
    }

    @Override
    public long sintercard(byte[]... keys) {
        return jedis.sintercard(keys);
    }

    @Override
    public long sintercard(int limit, byte[]... keys) {
        return jedis.sintercard(limit, keys);
    }

    @Override
    public Set<byte[]> sunion(byte[]... keys) {
        return jedis.sunion(keys);
    }

    @Override
    public long sunionstore(byte[] dstkey, byte[]... keys) {
        return jedis.sunionstore(dstkey, keys);
    }

    @Override
    public Set<byte[]> sdiff(byte[]... keys) {
        return jedis.sdiff(keys);
    }

    @Override
    public long sdiffstore(byte[] dstkey, byte[]... keys) {
        return jedis.sdiffstore(dstkey, keys);
    }

    @Override
    public byte[] srandmember(byte[] key) {
        return jedis.srandmember(key);
    }

    @Override
    public List<byte[]> srandmember(byte[] key, int count) {
        return jedis.srandmember(key, count);
    }

    @Override
    public long zadd(byte[] key, double score, byte[] member) {
        return jedis.zadd(key, score, member);
    }

    @Override
    public long zadd(byte[] key, Map<byte[], Double> scoreMembers) {
        return jedis.zadd(key, scoreMembers);
    }

    @Override
    public List<byte[]> zrange(byte[] key, long start, long stop) {
        return jedis.zrange(key, start, stop);
    }

    @Override
    public long zrem(byte[] key, byte[]... members) {
        return jedis.zrem(key, members);
    }

    @Override
    public double zincrby(byte[] key, double increment, byte[] member) {
        return jedis.zincrby(key, increment, member);
    }

    @Override
    public Long zrank(byte[] key, byte[] member) {
        return jedis.zrank(key, member);
    }

    @Override
    public Long zrevrank(byte[] key, byte[] member) {
        return jedis.zrevrank(key, member);
    }

    @Override
    public List<byte[]> zrevrange(byte[] key, long start, long stop) {
        return jedis.zrevrange(key, start, stop);
    }

    @Override
    public byte[] zrandmember(byte[] key) {
        return jedis.zrandmember(key);
    }

    @Override
    public List<byte[]> zrandmember(byte[] key, long count) {
        return jedis.zrandmember(key, count);
    }

    @Override
    public long zcard(byte[] key) {
        return jedis.zcard(key);
    }

    @Override
    public Double zscore(byte[] key, byte[] member) {
        return jedis.zscore(key, member);
    }

    @Override
    public List<Double> zmscore(byte[] key, byte[]... members) {
        return jedis.zmscore(key, members);
    }

    @Override
    public String watch(byte[]... keys) {
        return jedis.watch(keys);
    }

    @Override
    public String unwatch() {
        return jedis.unwatch();
    }

    @Override
    public List<byte[]> sort(byte[] key) {
        return jedis.sort(key);
    }

    @Override
    public long sort(byte[] key, byte[] dstkey) {
        return jedis.sort(key, dstkey);
    }

    @Override
    public List<byte[]> blpop(int timeout, byte[]... keys) {
        return jedis.blpop(timeout, keys);
    }

    @Override
    public List<byte[]> brpop(int timeout, byte[]... keys) {
        return jedis.brpop(timeout, keys);
    }

    @Override
    public String auth(String password) {
        return jedis.auth(password);
    }

    @Override
    public String auth(String user, String password) {
        return jedis.auth(user, password);
    }

    @Override
    public long zcount(byte[] key, double min, double max) {
        return jedis.zcount(key, min, max);
    }

    @Override
    public long zcount(byte[] key, byte[] min, byte[] max) {
        return jedis.zcount(key, min, max);
    }

    @Override
    public List<byte[]> zdiff(byte[]... keys) {
        return jedis.zdiff(keys);
    }

    @Override
    public long zdiffstore(byte[] dstkey, byte[]... keys) {
        return jedis.zdiffstore(dstkey, keys);
    }

    @Override
    public List<byte[]> zrangeByScore(byte[] key, double min, double max) {
        return jedis.zrangeByScore(key, min, max);
    }

    @Override
    public List<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max) {
        return jedis.zrangeByScore(key, min, max);
    }

    @Override
    public List<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count) {
        return jedis.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public List<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max, int offset, int count) {
        return jedis.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public List<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
        return jedis.zrevrangeByScore(key, max, min);
    }

    @Override
    public List<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min) {
        return jedis.zrevrangeByScore(key, max, min);
    }

    @Override
    public List<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
        return jedis.zrevrangeByScore(key, max, min, offset, count);
    }

    @Override
    public List<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min, int offset, int count) {
        return jedis.zrevrangeByScore(key, max, min, offset, count);
    }

    @Override
    public long zremrangeByRank(byte[] key, long start, long stop) {
        return jedis.zremrangeByRank(key, start, stop);
    }

    @Override
    public long zremrangeByScore(byte[] key, double min, double max) {
        return jedis.zremrangeByScore(key, min, max);
    }

    @Override
    public long zremrangeByScore(byte[] key, byte[] min, byte[] max) {
        return jedis.zremrangeByScore(key, min, max);
    }

    @Override
    public long zunionstore(byte[] dstkey, byte[]... sets) {
        return jedis.zunionstore(dstkey, sets);
    }

    @Override
    public long zinterstore(byte[] dstkey, byte[]... sets) {
        return jedis.zinterstore(dstkey, sets);
    }

    @Override
    public long zintercard(byte[]... keys) {
        return jedis.zintercard(keys);
    }

    @Override
    public long zintercard(long limit, byte[]... keys) {
        return jedis.zintercard(limit, keys);
    }

    @Override
    public long zlexcount(byte[] key, byte[] min, byte[] max) {
        return jedis.zlexcount(key, min, max);
    }

    @Override
    public List<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max) {
        return jedis.zrangeByLex(key, min, max);
    }

    @Override
    public List<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max, int offset, int count) {
        return jedis.zrangeByLex(key, min, max, offset, count);
    }

    @Override
    public List<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min) {
        return jedis.zrevrangeByLex(key, max, min);
    }

    @Override
    public List<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min, int offset, int count) {
        return jedis.zrevrangeByLex(key, max, min, offset, count);
    }

    @Override
    public long zremrangeByLex(byte[] key, byte[] min, byte[] max) {
        return jedis.zremrangeByLex(key, min, max);
    }

    @Override
    public String save() {
        return jedis.save();
    }

    @Override
    public String bgsave() {
        return jedis.bgsave();
    }

    @Override
    public String bgsaveSchedule() {
        return jedis.bgsaveSchedule();
    }

    @Override
    public String bgrewriteaof() {
        return jedis.bgrewriteaof();
    }

    @Override
    public long lastsave() {
        return jedis.lastsave();
    }

    @Override
    public String shutdownAbort() {
        return jedis.shutdownAbort();
    }

    @Override
    public String info() {
        return jedis.info();
    }

    @Override
    public String info(String section) {
        return jedis.info();
    }

    @Override
    public String replicaof(String host, int port) {
        return jedis.replicaof(host, port);
    }

    @Override
    public String replicaofNoOne() {
        return jedis.replicaofNoOne();
    }

    @Override
    public List<Object> roleBinary() {
        return jedis.roleBinary();
    }

    @Override
    public Map<byte[], byte[]> configGet(byte[] pattern) {
        return jedis.configGet(pattern);
    }

    @Override
    public Map<byte[], byte[]> configGet(byte[]... patterns) {
        return jedis.configGet(patterns);
    }

    @Override
    public String configResetStat() {
        return jedis.configResetStat();
    }

    @Override
    public String configRewrite() {
        return jedis.configRewrite();
    }

    @Override
    public String configSet(byte[] parameter, byte[] value) {
        return jedis.configSet(parameter, value);
    }

    @Override
    public String configSet(byte[]... parameterValues) {
        return jedis.configSet(parameterValues);
    }

    @Override
    public String configSetBinary(Map<byte[], byte[]> parameterValues) {
        return jedis.configSetBinary(parameterValues);
    }

    @Override
    public long strlen(byte[] key) {
        return jedis.strlen(key);
    }

    @Override
    public long lpushx(byte[] key, byte[]... strings) {
        return jedis.lpushx(key, strings);
    }

    @Override
    public long persist(byte[] key) {
        return jedis.persist(key);
    }

    @Override
    public long rpushx(byte[] key, byte[]... strings) {
        return jedis.rpushx(key, strings);
    }

    @Override
    public byte[] echo(byte[] string) {
        return jedis.echo(string);
    }

    @Override
    public byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
        return jedis.brpoplpush(source, destination, timeout);
    }

    @Override
    public boolean setbit(byte[] key, long offset, boolean value) {
        return jedis.setbit(key, offset, value);
    }

    @Override
    public boolean getbit(byte[] key, long offset) {
        return jedis.getbit(key, offset);
    }

    @Override
    public long bitpos(byte[] key, boolean value) {
        return jedis.bitpos(key, value);
    }

    @Override
    public long setrange(byte[] key, long offset, byte[] value) {
        return jedis.setrange(key, offset, value);
    }

    @Override
    public byte[] getrange(byte[] key, long startOffset, long endOffset) {
        return jedis.getrange(key, startOffset, endOffset);
    }

    @Override
    public long publish(byte[] channel, byte[] message) {
        return jedis.publish(channel, message);
    }

    @Override
    public Object eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
        return jedis.eval(script, keys, args);
    }

    @Override
    public Object evalReadonly(byte[] script, List<byte[]> keys, List<byte[]> args) {
        return jedis.evalReadonly(script, keys, args);
    }

    @Override
    public Object eval(byte[] script, int keyCount, byte[]... params) {
        return jedis.eval(script, keyCount, params);
    }

    @Override
    public Object eval(byte[] script) {
        return jedis.eval(script);
    }

    @Override
    public Object evalsha(byte[] sha1) {
        return jedis.evalsha(sha1);
    }

    @Override
    public Object evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
        return jedis.evalsha(sha1, keys, args);
    }

    @Override
    public Object evalshaReadonly(byte[] sha1, List<byte[]> keys, List<byte[]> args) {
        return jedis.evalshaReadonly(sha1, keys, args);
    }

    @Override
    public Object evalsha(byte[] sha1, int keyCount, byte[]... params) {
        return jedis.evalsha(sha1, keyCount, params);
    }

    @Override
    public String scriptFlush() {
        return jedis.scriptFlush();
    }

    @Override
    public Boolean scriptExists(byte[] sha1) {
        return jedis.scriptExists(sha1);
    }

    @Override
    public List<Boolean> scriptExists(byte[]... sha1) {
        return jedis.scriptExists(sha1);
    }

    @Override
    public byte[] scriptLoad(byte[] script) {
        return jedis.scriptLoad(script);
    }

    @Override
    public String scriptKill() {
        return jedis.scriptKill();
    }

    @Override
    public String slowlogReset() {
        return jedis.slowlogReset();
    }

    @Override
    public long slowlogLen() {
        return jedis.slowlogLen();
    }

    @Override
    public List<Object> slowlogGetBinary() {
        return jedis.slowlogGetBinary();
    }

    @Override
    public List<Object> slowlogGetBinary(long entries) {
        return jedis.slowlogGetBinary(entries);
    }

    @Override
    public Long objectRefcount(byte[] key) {
        return jedis.objectRefcount(key);
    }

    @Override
    public byte[] objectEncoding(byte[] key) {
        return jedis.objectEncoding(key);
    }

    @Override
    public Long objectIdletime(byte[] key) {
        return jedis.objectIdletime(key);
    }

    @Override
    public List<byte[]> objectHelpBinary() {
        return jedis.objectHelpBinary();
    }

    @Override
    public Long objectFreq(byte[] key) {
        return jedis.objectFreq(key);
    }

    @Override
    public long bitcount(byte[] key) {
        return jedis.bitcount(key);
    }

    @Override
    public long bitcount(byte[] key, long start, long end) {
        return jedis.bitcount(key, start, end);
    }

    @Override
    public byte[] dump(byte[] key) {
        return jedis.dump(key);
    }

    @Override
    public String restore(byte[] key, long ttl, byte[] serializedValue) {
        return jedis.restore(key, ttl, serializedValue);
    }

    @Override
    public long pttl(byte[] key) {
        return jedis.pttl(key);
    }

    @Override
    public String psetex(byte[] key, long milliseconds, byte[] value) {
        return jedis.psetex(key, milliseconds, value);
    }

    @Override
    public byte[] memoryDoctorBinary() {
        return jedis.memoryDoctorBinary();
    }

    @Override
    public Long memoryUsage(byte[] key) {
        return jedis.memoryUsage(key);
    }

    @Override
    public Long memoryUsage(byte[] key, int samples) {
        return jedis.memoryUsage(key, samples);
    }

    @Override
    public String failover() {
        return jedis.failover();
    }

    @Override
    public String failoverAbort() {
        return jedis.failoverAbort();
    }

    @Override
    public byte[] aclWhoAmIBinary() {
        return jedis.aclWhoAmIBinary();
    }

    @Override
    public byte[] aclGenPassBinary() {
        return jedis.aclGenPassBinary();
    }

    @Override
    public byte[] aclGenPassBinary(int bits) {
        return jedis.aclGenPassBinary();
    }

    @Override
    public List<byte[]> aclListBinary() {
        return jedis.aclListBinary();
    }

    @Override
    public List<byte[]> aclUsersBinary() {
        return jedis.aclUsersBinary();
    }

    @Override
    public String aclSetUser(byte[] name) {
        return jedis.aclSetUser(name);
    }

    @Override
    public String aclSetUser(byte[] name, byte[]... rules) {
        return jedis.aclSetUser(name, rules);
    }

    @Override
    public long aclDelUser(byte[]... names) {
        return jedis.aclDelUser(names);
    }

    @Override
    public List<byte[]> aclCatBinary() {
        return jedis.aclCatBinary();
    }

    @Override
    public List<byte[]> aclCat(byte[] category) {
        return jedis.aclCat(category);
    }

    @Override
    public List<byte[]> aclLogBinary() {
        return jedis.aclLogBinary();
    }

    @Override
    public List<byte[]> aclLogBinary(int limit) {
        return jedis.aclLogBinary(limit);
    }

    @Override
    public String aclLogReset() {
        return jedis.aclLogReset();
    }

    @Override
    public String clientKill(byte[] ipPort) {
        return jedis.clientKill(ipPort);
    }

    @Override
    public String clientKill(String ip, int port) {
        return jedis.clientKill(ip, port);
    }

    @Override
    public byte[] clientGetnameBinary() {
        return jedis.clientGetnameBinary();
    }

    @Override
    public byte[] clientListBinary() {
        return jedis.clientListBinary();
    }

    @Override
    public byte[] clientListBinary(long... clientIds) {
        return jedis.clientListBinary();
    }

    @Override
    public byte[] clientInfoBinary() {
        return jedis.clientInfoBinary();
    }

    @Override
    public String clientSetname(byte[] name) {
        return jedis.clientSetname(name);
    }

    @Override
    public long clientId() {
        return jedis.clientId();
    }

    @Override
    public long clientUnblock(long clientId) {
        return jedis.clientUnblock(clientId);
    }

    @Override
    public String clientPause(long timeout) {
        return jedis.clientPause(timeout);
    }

    @Override
    public String clientUnpause() {
        return jedis.clientUnpause();
    }

    @Override
    public String clientNoEvictOn() {
        return jedis.clientNoEvictOn();
    }

    @Override
    public String clientNoEvictOff() {
        return jedis.clientNoEvictOff();
    }

    @Override
    public String clientNoTouchOn() {
        return jedis.clientNoTouchOn();
    }

    @Override
    public String clientNoTouchOff() {
        return jedis.clientNoTouchOff();
    }

    @Override
    public List<String> time() {
        return jedis.time();
    }

    @Override
    public String migrate(String host, int port, byte[] key, int destinationDb, int timeout) {
        return jedis.migrate(host, port, key, destinationDb, timeout);
    }

    @Override
    public String migrate(String host, int port, byte[] key, int timeout) {
        return jedis.migrate(host, port, key, timeout);
    }

    @Override
    public long waitReplicas(int replicas, long timeout) {
        return jedis.waitReplicas(replicas, timeout);
    }

    @Override
    public long pfadd(byte[] key, byte[]... elements) {
        return jedis.pfadd(key, elements);
    }

    @Override
    public long pfcount(byte[] key) {
        return jedis.pfcount(key);
    }

    @Override
    public String pfmerge(byte[] destkey, byte[]... sourcekeys) {
        return jedis.pfmerge(destkey, sourcekeys);
    }

    @Override
    public long pfcount(byte[]... keys) {
        return jedis.pfcount(keys);
    }

    @Override
    public long geoadd(byte[] key, double longitude, double latitude, byte[] member) {
        return jedis.geoadd(key, longitude, latitude, member);
    }

    @Override
    public Double geodist(byte[] key, byte[] member1, byte[] member2) {
        return jedis.geodist(key, member1, member2);
    }

    @Override
    public List<byte[]> geohash(byte[] key, byte[]... members) {
        return jedis.geohash(key, members);
    }

    @Override
    public List<Long> bitfield(byte[] key, byte[]... arguments) {
        return jedis.bitfield(key, arguments);
    }

    @Override
    public List<Long> bitfieldReadonly(byte[] key, byte[]... arguments) {
        return jedis.bitfieldReadonly(key, arguments);
    }

    @Override
    public long hstrlen(byte[] key, byte[] field) {
        return jedis.hstrlen(key, field);
    }

    @Override
    public List<Long> hexpire(byte[] key, long seconds, byte[]... fields) {
        return jedis.hexpire(key, seconds, fields);
    }

    @Override
    public List<Long> hpexpire(byte[] key, long milliseconds, byte[]... fields) {
        return jedis.hexpire(key, milliseconds, fields);
    }

    @Override
    public List<Long> hexpireAt(byte[] key, long unixTimeSeconds, byte[]... fields) {
        return jedis.hexpireAt(key, unixTimeSeconds, fields);
    }

    @Override
    public List<Long> hpexpireAt(byte[] key, long unixTimeMillis, byte[]... fields) {
        return jedis.hpexpireAt(key, unixTimeMillis, fields);
    }

    @Override
    public List<Long> hexpireTime(byte[] key, byte[]... fields) {
        return jedis.hexpireTime(key, fields);
    }

    @Override
    public List<Long> hpexpireTime(byte[] key, byte[]... fields) {
        return jedis.hpexpireTime(key, fields);
    }

    @Override
    public List<Long> httl(byte[] key, byte[]... fields) {
        return jedis.httl(key, fields);
    }

    @Override
    public List<Long> hpttl(byte[] key, byte[]... fields) {
        return jedis.hpttl(key, fields);
    }

    @Override
    public List<Long> hpersist(byte[] key, byte[]... fields) {
        return jedis.hpersist(key, fields);
    }

    @Override
    public long xlen(byte[] key) {
        return jedis.xlen(key);
    }

    @Override
    public List<Object> xrange(byte[] key, byte[] start, byte[] end) {
        return jedis.xrange(key, start, end);
    }

    @Override
    public List<Object> xrange(byte[] key, byte[] start, byte[] end, int count) {
        return jedis.xrange(key, start, end, count);
    }

    @Override
    public List<Object> xrevrange(byte[] key, byte[] end, byte[] start) {
        return jedis.xrevrange(key, end, start);
    }

    @Override
    public List<Object> xrevrange(byte[] key, byte[] end, byte[] start, int count) {
        return jedis.xrevrange(key, end, start, count);
    }

    @Override
    public long xack(byte[] key, byte[] group, byte[]... ids) {
        return jedis.xack(key, group, ids);
    }

    @Override
    public String xgroupCreate(byte[] key, byte[] consumer, byte[] id, boolean makeStream) {
        return jedis.xgroupCreate(key, consumer, id, makeStream);
    }

    @Override
    public String xgroupSetID(byte[] key, byte[] consumer, byte[] id) {
        return jedis.xgroupSetID(key, consumer, id);
    }

    @Override
    public long xgroupDestroy(byte[] key, byte[] consumer) {
        return jedis.xgroupDestroy(key, consumer);
    }

    @Override
    public boolean xgroupCreateConsumer(byte[] key, byte[] groupName, byte[] consumerName) {
        return jedis.xgroupCreateConsumer(key, groupName, consumerName);
    }

    @Override
    public long xgroupDelConsumer(byte[] key, byte[] groupName, byte[] consumerName) {
        return jedis.xgroupDelConsumer(key, groupName, consumerName);
    }

    @Override
    public long xdel(byte[] key, byte[]... ids) {
        return jedis.xdel(key, ids);
    }

    @Override
    public long xtrim(byte[] key, long maxLen, boolean approximateLength) {
        return jedis.xtrim(key, maxLen, approximateLength);
    }

    @Override
    public Object xpending(byte[] key, byte[] groupName) {
        return jedis.xpending(key, groupName);
    }

    @Override
    public Object xinfoStream(byte[] key) {
        return jedis.xinfoStream(key);
    }

    @Override
    public Object xinfoStreamFull(byte[] key) {
        return jedis.xinfoStreamFull(key);
    }

    @Override
    public Object xinfoStreamFull(byte[] key, int count) {
        return jedis.xinfoStreamFull(key, count);
    }

    @Override
    public List<Object> xinfoGroups(byte[] key) {
        return jedis.xinfoGroups(key);
    }

    @Override
    public List<Object> xinfoConsumers(byte[] key, byte[] group) {
        return jedis.xinfoConsumers(key, group);
    }

    @Override
    public boolean copy(String srcKey, String dstKey, int db, boolean replace) {
        return jedis.copy(srcKey, dstKey, db, replace);
    }

    @Override
    public boolean copy(String srcKey, String dstKey, boolean replace) {
        return jedis.copy(srcKey, dstKey, replace);
    }

    @Override
    public String ping(String message) {
        return jedis.ping();
    }

    @Override
    public String set(String key, String value) {
        return jedis.set(key, value);
    }

    @Override
    public String get(String key) {
        return jedis.get(key);
    }

    @Override
    public String setGet(String key, String value) {
        return jedis.setGet(key, value);
    }

    @Override
    public String getDel(String key) {
        return jedis.getDel(key);
    }

    @Override
    public long exists(String... keys) {
        return jedis.exists(keys);
    }

    @Override
    public boolean exists(String key) {
        return jedis.exists(key);
    }

    @Override
    public long del(String... keys) {
        return jedis.del(keys);
    }

    @Override
    public long del(String key) {
        return jedis.del(key);
    }

    @Override
    public long unlink(String... keys) {
        return jedis.unlink(keys);
    }

    @Override
    public long unlink(String key) {
        return jedis.unlink(key);
    }

    @Override
    public String type(String key) {
        return jedis.type(key);
    }

    @Override
    public Set<String> keys(String pattern) {
        return jedis.keys(pattern);
    }

    @Override
    public String randomKey() {
        return jedis.randomKey();
    }

    @Override
    public String rename(String oldkey, String newkey) {
        return jedis.rename(oldkey, newkey);
    }

    @Override
    public long renamenx(String oldkey, String newkey) {
        return jedis.renamenx(oldkey, newkey);
    }

    @Override
    public long expire(String key, long seconds) {
        return jedis.expire(key, seconds);
    }

    @Override
    public long pexpire(String key, long milliseconds) {
        return jedis.pexpire(key, milliseconds);
    }

    @Override
    public long expireTime(String key) {
        return jedis.expireTime(key);
    }

    @Override
    public long pexpireTime(String key) {
        return jedis.pexpireTime(key);
    }

    @Override
    public long expireAt(String key, long unixTime) {
        return jedis.expireAt(key, unixTime);
    }

    @Override
    public long pexpireAt(String key, long millisecondsTimestamp) {
        return jedis.pexpireAt(key, millisecondsTimestamp);
    }

    @Override
    public long ttl(String key) {
        return jedis.ttl(key);
    }

    @Override
    public long touch(String... keys) {
        return jedis.touch(keys);
    }

    @Override
    public long touch(String key) {
        return jedis.touch(key);
    }

    @Override
    public long move(String key, int dbIndex) {
        return jedis.move(key, dbIndex);
    }

    @Override
    public List<String> mget(String... keys) {
        return jedis.mget(keys);
    }

    @Override
    public long setnx(String key, String value) {
        return jedis.setnx(key, value);
    }

    @Override
    public String setex(String key, long seconds, String value) {
        return jedis.setex(key, seconds, value);
    }

    @Override
    public String mset(String... keysvalues) {
        return jedis.mset(keysvalues);
    }

    @Override
    public long msetnx(String... keysvalues) {
        return jedis.msetnx(keysvalues);
    }

    @Override
    public long decrBy(String key, long decrement) {
        return jedis.decrBy(key, decrement);
    }

    @Override
    public long decr(String key) {
        return jedis.decr(key);
    }

    @Override
    public long incrBy(String key, long increment) {
        return jedis.incrBy(key, increment);
    }

    @Override
    public double incrByFloat(String key, double increment) {
        return jedis.incrByFloat(key, increment);
    }

    @Override
    public long incr(String key) {
        return jedis.incr(key);
    }

    @Override
    public long append(String key, String value) {
        return jedis.append(key, value);
    }

    @Override
    public String substr(String key, int start, int end) {
        return jedis.substr(key, start, end);
    }

    @Override
    public long hset(String key, String field, String value) {
        return jedis.hset(key, field, value);
    }

    @Override
    public long hset(String key, Map<String, String> hash) {
        return jedis.hset(key, hash);
    }

    @Override
    public String hget(String key, String field) {
        return jedis.hget(key, field);
    }

    @Override
    public List<String> hgetdel(String key, String... fields) {
        return jedis.hgetdel(key, fields);
    }

    @Override
    public long hsetnx(String key, String field, String value) {
        return jedis.hsetnx(key, field, value);
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        return jedis.hmset(key, hash);
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return jedis.hmget(key, fields);
    }

    @Override
    public long hincrBy(String key, String field, long value) {
        return jedis.hincrBy(key, field, value);
    }

    @Override
    public double hincrByFloat(String key, String field, double value) {
        return jedis.hincrByFloat(key, field, value);
    }

    @Override
    public boolean hexists(String key, String field) {
        return jedis.hexists(key, field);
    }

    @Override
    public long hdel(String key, String... fields) {
        return jedis.hdel(key, fields);
    }

    @Override
    public long hlen(String key) {
        return jedis.hlen(key);
    }

    @Override
    public Set<String> hkeys(String key) {
        return jedis.hkeys(key);
    }

    @Override
    public List<String> hvals(String key) {
        return jedis.hvals(key);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return jedis.hgetAll(key);
    }

    @Override
    public String hrandfield(String key) {
        return jedis.hrandfield(key);
    }

    @Override
    public List<String> hrandfield(String key, long count) {
        return jedis.hrandfield(key, count);
    }

    @Override
    public List<Map.Entry<String, String>> hrandfieldWithValues(String key, long count) {
        return jedis.hrandfieldWithValues(key, count);
    }

    @Override
    public long rpush(String key, String... strings) {
        return jedis.rpush(key, strings);
    }

    @Override
    public long lpush(String key, String... strings) {
        return jedis.lpush(key, strings);
    }

    @Override
    public long llen(String key) {
        return jedis.llen(key);
    }

    @Override
    public List<String> lrange(String key, long start, long stop) {
        return jedis.lrange(key, start, stop);
    }

    @Override
    public String ltrim(String key, long start, long stop) {
        return jedis.ltrim(key, start, stop);
    }

    @Override
    public String lindex(String key, long index) {
        return jedis.lindex(key, index);
    }

    @Override
    public String lset(String key, long index, String value) {
        return jedis.lset(key, index, value);
    }

    @Override
    public long lrem(String key, long count, String value) {
        return jedis.lrem(key, count, value);
    }

    @Override
    public String lpop(String key) {
        return jedis.lpop(key);
    }

    @Override
    public List<String> lpop(String key, int count) {
        return jedis.lpop(key, count);
    }

    @Override
    public Long lpos(String key, String element) {
        return jedis.lpos(key, element);
    }

    @Override
    public String rpop(String key) {
        return jedis.rpop(key);
    }

    @Override
    public List<String> rpop(String key, int count) {
        return jedis.rpop(key, count);
    }

    @Override
    public String rpoplpush(String srckey, String dstkey) {
        return jedis.rpoplpush(srckey, dstkey);
    }

    @Override
    public long sadd(String key, String... members) {
        return jedis.sadd(key, members);
    }

    @Override
    public Set<String> smembers(String key) {
        return jedis.smembers(key);
    }

    @Override
    public long srem(String key, String... members) {
        return jedis.srem(key, members);
    }

    @Override
    public String spop(String key) {
        return jedis.spop(key);
    }

    @Override
    public Set<String> spop(String key, long count) {
        return jedis.spop(key, count);
    }

    @Override
    public long smove(String srckey, String dstkey, String member) {
        return jedis.smove(srckey, dstkey, member);
    }

    @Override
    public long scard(String key) {
        return jedis.scard(key);
    }

    @Override
    public boolean sismember(String key, String member) {
        return jedis.sismember(key, member);
    }

    @Override
    public List<Boolean> smismember(String key, String... members) {
        return jedis.smismember(key, members);
    }

    @Override
    public Set<String> sinter(String... keys) {
        return jedis.sinter(keys);
    }

    @Override
    public long sinterstore(String dstkey, String... keys) {
        return jedis.sinterstore(dstkey, keys);
    }

    @Override
    public long sintercard(String... keys) {
        return jedis.sintercard(keys);
    }

    @Override
    public long sintercard(int limit, String... keys) {
        return jedis.sintercard(limit, keys);
    }

    @Override
    public Set<String> sunion(String... keys) {
        return jedis.sunion(keys);
    }

    @Override
    public long sunionstore(String dstkey, String... keys) {
        return jedis.sunionstore(dstkey, keys);
    }

    @Override
    public Set<String> sdiff(String... keys) {
        return jedis.sdiff(keys);
    }

    @Override
    public long sdiffstore(String dstkey, String... keys) {
        return jedis.sdiffstore(dstkey, keys);
    }

    @Override
    public String srandmember(String key) {
        return jedis.srandmember(key);
    }

    @Override
    public List<String> srandmember(String key, int count) {
        return jedis.srandmember(key, count);
    }

    @Override
    public long zadd(String key, double score, String member) {
        return jedis.zadd(key, score, member);
    }

    @Override
    public long zadd(String key, Map<String, Double> scoreMembers) {
        return jedis.zadd(key, scoreMembers);
    }

    @Override
    public List<String> zdiff(String... keys) {
        return jedis.zdiff(keys);
    }

    @Override
    public long zdiffstore(String dstkey, String... keys) {
        return jedis.zdiffstore(dstkey, keys);
    }

    @Override
    public List<String> zrange(String key, long start, long stop) {
        return jedis.zrange(key, start, stop);
    }

    @Override
    public long zrem(String key, String... members) {
        return jedis.zrem(key, members);
    }

    @Override
    public double zincrby(String key, double increment, String member) {
        return jedis.zincrby(key, increment, member);
    }

    @Override
    public Long zrank(String key, String member) {
        return jedis.zrank(key, member);
    }

    @Override
    public Long zrevrank(String key, String member) {
        return jedis.zrevrank(key, member);
    }

    @Override
    public List<String> zrevrange(String key, long start, long stop) {
        return jedis.zrevrange(key, start, stop);
    }

    @Override
    public String zrandmember(String key) {
        return jedis.zrandmember(key);
    }

    @Override
    public List<String> zrandmember(String key, long count) {
        return jedis.zrandmember(key, count);
    }

    @Override
    public long zcard(String key) {
        return jedis.zcard(key);
    }

    @Override
    public Double zscore(String key, String member) {
        return jedis.zscore(key, member);
    }

    @Override
    public List<Double> zmscore(String key, String... members) {
        return jedis.zmscore(key, members);
    }

    @Override
    public String watch(String... keys) {
        return jedis.watch(keys);
    }

    @Override
    public List<String> sort(String key) {
        return jedis.sort(key);
    }

    @Override
    public long sort(String key, String dstkey) {
        return jedis.sort(key, dstkey);
    }

    @Override
    public List<String> blpop(int timeout, String... keys) {
        return jedis.blpop(timeout, keys);
    }

    @Override
    public List<String> brpop(int timeout, String... keys) {
        return jedis.brpop(timeout, keys);
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        return jedis.blpop(timeout, key);
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        return jedis.brpop(timeout, key);
    }

    @Override
    public long zcount(String key, double min, double max) {
        return jedis.zcount(key, min, max);
    }

    @Override
    public long zcount(String key, String min, String max) {
        return jedis.zcount(key, min, max);
    }

    @Override
    public List<String> zrangeByScore(String key, double min, double max) {
        return jedis.zrangeByScore(key, min, max);
    }

    @Override
    public List<String> zrangeByScore(String key, String min, String max) {
        return jedis.zrangeByScore(key, min, max);
    }

    @Override
    public List<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return jedis.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public List<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return jedis.zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public List<String> zrevrangeByScore(String key, double max, double min) {
        return jedis.zrevrangeByScore(key, max, min);
    }

    @Override
    public List<String> zrevrangeByScore(String key, String max, String min) {
        return jedis.zrevrangeByScore(key, max, min);
    }

    @Override
    public List<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return jedis.zrevrangeByScore(key, max, min, offset, count);
    }

    @Override
    public List<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return jedis.zrevrangeByScore(key, max, min, offset, count);
    }

    @Override
    public long zremrangeByRank(String key, long start, long stop) {
        return jedis.zremrangeByRank(key, start, stop);
    }

    @Override
    public long zremrangeByScore(String key, double min, double max) {
        return jedis.zremrangeByScore(key, min, max);
    }

    @Override
    public long zremrangeByScore(String key, String min, String max) {
        return jedis.zremrangeByScore(key, min, max);
    }

    @Override
    public long zunionstore(String dstkey, String... sets) {
        return jedis.zunionstore(dstkey, sets);
    }

    @Override
    public long zintercard(String... keys) {
        return jedis.zintercard(keys);
    }

    @Override
    public long zintercard(long limit, String... keys) {
        return jedis.zintercard(limit, keys);
    }

    @Override
    public long zinterstore(String dstkey, String... sets) {
        return jedis.zinterstore(dstkey, sets);
    }

    @Override
    public long zlexcount(String key, String min, String max) {
        return jedis.zlexcount(key, min, max);
    }

    @Override
    public List<String> zrangeByLex(String key, String min, String max) {
        return jedis.zrangeByLex(key, min, max);
    }

    @Override
    public List<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return jedis.zrangeByLex(key, min, max, offset, count);
    }

    @Override
    public List<String> zrevrangeByLex(String key, String max, String min) {
        return jedis.zrevrangeByLex(key, max, min);
    }

    @Override
    public List<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return jedis.zrevrangeByLex(key, max, min, offset, count);
    }

    @Override
    public long zremrangeByLex(String key, String min, String max) {
        return jedis.zremrangeByLex(key, min, max);
    }

    @Override
    public long strlen(String key) {
        return jedis.strlen(key);
    }

    @Override
    public long lpushx(String key, String... strings) {
        return jedis.lpushx(key, strings);
    }

    @Override
    public long persist(String key) {
        return jedis.persist(key);
    }

    @Override
    public long rpushx(String key, String... strings) {
        return jedis.rpushx(key, strings);
    }

    @Override
    public String echo(String string) {
        return jedis.echo(string);
    }

    @Override
    public String brpoplpush(String source, String destination, int timeout) {
        return jedis.brpoplpush(source, destination, timeout);
    }

    @Override
    public boolean setbit(String key, long offset, boolean value) {
        return jedis.setbit(key, offset, value);
    }

    @Override
    public boolean getbit(String key, long offset) {
        return jedis.getbit(key, offset);
    }

    @Override
    public long setrange(String key, long offset, String value) {
        return jedis.setrange(key, offset, value);
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return jedis.getrange(key, startOffset, endOffset);
    }

    @Override
    public long bitpos(String key, boolean value) {
        return jedis.bitpos(key, value);
    }

    @Override
    public List<Object> role() {
        return jedis.role();
    }

    @Override
    public Map<String, String> configGet(String pattern) {
        return jedis.configGet(pattern);
    }

    @Override
    public Map<String, String> configGet(String... patterns) {
        return jedis.configGet(patterns);
    }

    @Override
    public String configSet(String parameter, String value) {
        return jedis.configSet(parameter, value);
    }

    @Override
    public String configSet(String... parameterValues) {
        return jedis.configSet(parameterValues);
    }

    @Override
    public String configSet(Map<String, String> parameterValues) {
        return jedis.configSet(parameterValues);
    }

    @Override
    public long publish(String channel, String message) {
        return jedis.publish(channel, message);
    }

    @Override
    public List<String> pubsubChannels() {
        return jedis.pubsubChannels();
    }

    @Override
    public List<String> pubsubChannels(String pattern) {
        return jedis.pubsubChannels();
    }

    @Override
    public Long pubsubNumPat() {
        return jedis.pubsubNumPat();
    }

    @Override
    public Map<String, Long> pubsubNumSub(String... channels) {
        return jedis.pubsubNumSub();
    }

    @Override
    public List<String> pubsubShardChannels() {
        return jedis.pubsubShardChannels();
    }

    @Override
    public List<String> pubsubShardChannels(String pattern) {
        return jedis.pubsubShardChannels();
    }

    @Override
    public Map<String, Long> pubsubShardNumSub(String... channels) {
        return jedis.pubsubShardNumSub();
    }

    @Override
    public Object eval(String script, int keyCount, String... params) {
        return jedis.eval(script, keyCount, params);
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        return jedis.eval(script, keys, args);
    }

    @Override
    public Object evalReadonly(String script, List<String> keys, List<String> args) {
        return jedis.evalReadonly(script, keys, args);
    }

    @Override
    public Object eval(String script) {
        return jedis.eval(script);
    }

    @Override
    public Object evalsha(String sha1) {
        return jedis.evalsha(sha1);
    }

    @Override
    public Object evalsha(String sha1, List<String> keys, List<String> args) {
        return jedis.evalsha(sha1, keys, args);
    }

    @Override
    public Object evalshaReadonly(String sha1, List<String> keys, List<String> args) {
        return jedis.evalshaReadonly(sha1, keys, args);
    }

    @Override
    public Object evalsha(String sha1, int keyCount, String... params) {
        return jedis.evalsha(sha1, keyCount, params);
    }

    @Override
    public Boolean scriptExists(String sha1) {
        return jedis.scriptExists(sha1);
    }

    @Override
    public List<Boolean> scriptExists(String... sha1) {
        return jedis.scriptExists(sha1);
    }

    @Override
    public String scriptLoad(String script) {
        return jedis.scriptLoad(script);
    }

    @Override
    public Long objectRefcount(String key) {
        return jedis.objectRefcount(key);
    }

    @Override
    public String objectEncoding(String key) {
        return jedis.objectEncoding(key);
    }

    @Override
    public Long objectIdletime(String key) {
        return jedis.objectIdletime(key);
    }

    @Override
    public List<String> objectHelp() {
        return jedis.objectHelp();
    }

    @Override
    public Long objectFreq(String key) {
        return jedis.objectFreq(key);
    }

    @Override
    public long bitcount(String key) {
        return jedis.bitcount(key);
    }

    @Override
    public long bitcount(String key, long start, long end) {
        return jedis.bitcount(key, start, end);
    }

    @Override
    public long commandCount() {
        return jedis.commandCount();
    }

    @Override
    public List<String> commandGetKeys(String... command) {
        return jedis.commandGetKeys();
    }

    @Override
    public List<String> commandList() {
        return jedis.commandList();
    }

    @Override
    public String sentinelMyId() {
        return jedis.sentinelMyId();
    }

    @Override
    public List<Map<String, String>> sentinelMasters() {
        return jedis.sentinelMasters();
    }

    @Override
    public Map<String, String> sentinelMaster(String masterName) {
        return jedis.sentinelMaster(masterName);
    }

    @Override
    public List<Map<String, String>> sentinelSentinels(String masterName) {
        return jedis.sentinelSentinels(masterName);
    }

    @Override
    public List<String> sentinelGetMasterAddrByName(String masterName) {
        return jedis.sentinelGetMasterAddrByName(masterName);
    }

    @Override
    public Long sentinelReset(String pattern) {
        return jedis.sentinelReset(pattern);
    }

    @Override
    public List<Map<String, String>> sentinelReplicas(String masterName) {
        return jedis.sentinelReplicas(masterName);
    }

    @Override
    public String sentinelFailover(String masterName) {
        return jedis.sentinelFailover(masterName);
    }

    @Override
    public String sentinelMonitor(String masterName, String ip, int port, int quorum) {
        return jedis.sentinelMonitor(masterName, ip, port, quorum);
    }

    @Override
    public String sentinelRemove(String masterName) {
        return jedis.sentinelRemove(masterName);
    }

    @Override
    public String sentinelSet(String masterName, Map<String, String> parameterMap) {
        return jedis.sentinelSet(masterName, parameterMap);
    }

    @Override
    public byte[] dump(String key) {
        return jedis.dump(key);
    }

    @Override
    public String restore(String key, long ttl, byte[] serializedValue) {
        return jedis.restore(key, ttl, serializedValue);
    }

    @Override
    public long pttl(String key) {
        return jedis.pttl(key);
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        return jedis.psetex(key, milliseconds, value);
    }

    @Override
    public String aclSetUser(String name) {
        return jedis.aclSetUser(name);
    }

    @Override
    public String aclSetUser(String name, String... rules) {
        return jedis.aclSetUser(name, rules);
    }

    @Override
    public long aclDelUser(String... names) {
        return jedis.aclDelUser(names);
    }

    @Override
    public List<String> aclUsers() {
        return jedis.aclUsers();
    }

    @Override
    public List<String> aclList() {
        return jedis.aclList();
    }

    @Override
    public String aclWhoAmI() {
        return jedis.aclWhoAmI();
    }

    @Override
    public List<String> aclCat() {
        return jedis.aclCat();
    }

    @Override
    public List<String> aclCat(String category) {
        return jedis.aclCat();
    }

    @Override
    public String aclLoad() {
        return jedis.aclLoad();
    }

    @Override
    public String aclSave() {
        return jedis.aclSave();
    }

    @Override
    public String aclGenPass() {
        return jedis.aclGenPass();
    }

    @Override
    public String aclGenPass(int bits) {
        return jedis.aclGenPass();
    }

    @Override
    public String aclDryRun(String username, String command, String... args) {
        return jedis.aclDryRun(username, command, args);
    }

    @Override
    public byte[] aclDryRunBinary(byte[] username, byte[] command, byte[]... args) {
        return jedis.aclDryRunBinary(username, command, args);
    }

    @Override
    public String clientKill(String ipPort) {
        return jedis.clientKill(ipPort);
    }

    @Override
    public String clientGetname() {
        return jedis.clientGetname();
    }

    @Override
    public String clientList() {
        return jedis.clientList();
    }

    @Override
    public String clientList(long... clientIds) {
        return jedis.clientList();
    }

    @Override
    public String clientInfo() {
        return jedis.clientInfo();
    }

    @Override
    public String clientSetname(String name) {
        return jedis.clientSetname(name);
    }

    @Override
    public String migrate(String host, int port, String key, int destinationDb, int timeout) {
        return jedis.migrate(host, port, key, destinationDb, timeout);
    }

    @Override
    public String migrate(String host, int port, String key, int timeout) {
        return jedis.migrate(host, port, key, timeout);
    }

    @Override
    public String readonly() {
        return jedis.readonly();
    }

    @Override
    public String readwrite() {
        return jedis.readwrite();
    }

    @Override
    public String clusterNodes() {
        return jedis.clusterNodes();
    }

    @Override
    public String clusterMeet(String ip, int port) {
        return jedis.clusterMeet(ip, port);
    }

    @Override
    public String clusterReset() {
        return jedis.clusterReset();
    }

    @Override
    public String clusterAddSlots(int... slots) {
        return jedis.clusterAddSlots();
    }

    @Override
    public String clusterDelSlots(int... slots) {
        return jedis.clusterDelSlots();
    }

    @Override
    public String clusterInfo() {
        return jedis.clusterInfo();
    }

    @Override
    public List<String> clusterGetKeysInSlot(int slot, int count) {
        return jedis.clusterGetKeysInSlot(slot, count);
    }

    @Override
    public List<byte[]> clusterGetKeysInSlotBinary(int slot, int count) {
        return jedis.clusterGetKeysInSlotBinary(slot, count);
    }

    @Override
    public String clusterSetSlotNode(int slot, String nodeId) {
        return jedis.clusterSetSlotNode(slot, nodeId);
    }

    @Override
    public String clusterSetSlotMigrating(int slot, String nodeId) {
        return jedis.clusterSetSlotMigrating(slot, nodeId);
    }

    @Override
    public String clusterSetSlotImporting(int slot, String nodeId) {
        return jedis.clusterSetSlotImporting(slot, nodeId);
    }

    @Override
    public String clusterSetSlotStable(int slot) {
        return jedis.clusterSetSlotStable(slot);
    }

    @Override
    public String clusterForget(String nodeId) {
        return jedis.clusterForget(nodeId);
    }

    @Override
    public String clusterFlushSlots() {
        return jedis.clusterFlushSlots();
    }

    @Override
    public long clusterKeySlot(String key) {
        return jedis.clusterKeySlot(key);
    }

    @Override
    public long clusterCountFailureReports(String nodeId) {
        return jedis.clusterCountFailureReports(nodeId);
    }

    @Override
    public long clusterCountKeysInSlot(int slot) {
        return jedis.clusterCountKeysInSlot(slot);
    }

    @Override
    public String clusterSaveConfig() {
        return jedis.clusterSaveConfig();
    }

    @Override
    public String clusterSetConfigEpoch(long configEpoch) {
        return jedis.clusterSetConfigEpoch(configEpoch);
    }

    @Override
    public String clusterBumpEpoch() {
        return jedis.clusterBumpEpoch();
    }

    @Override
    public String clusterReplicate(String nodeId) {
        return jedis.clusterReplicate(nodeId);
    }

    @Override
    public List<String> clusterReplicas(String nodeId) {
        return jedis.clusterReplicas(nodeId);
    }

    @Override
    public String clusterFailover() {
        return jedis.clusterFailover();
    }

    @Override
    public String clusterMyId() {
        return jedis.clusterMyId();
    }

    @Override
    public String clusterMyShardId() {
        return jedis.clusterMyShardId();
    }

    @Override
    public List<Map<String, Object>> clusterLinks() {
        return jedis.clusterLinks();
    }

    @Override
    public String clusterAddSlotsRange(int... ranges) {
        return jedis.clusterAddSlotsRange(ranges);
    }

    @Override
    public String clusterDelSlotsRange(int... ranges) {
        return jedis.clusterDelSlotsRange(ranges);
    }

    @Override
    public String asking() {
        return jedis.asking();
    }

    @Override
    public long pfadd(String key, String... elements) {
        return jedis.pfadd(key, elements);
    }

    @Override
    public long pfcount(String key) {
        return jedis.pfcount(key);
    }

    @Override
    public long pfcount(String... keys) {
        return jedis.pfcount(keys);
    }

    @Override
    public String pfmerge(String destkey, String... sourcekeys) {
        return jedis.pfmerge(destkey, sourcekeys);
    }

    @Override
    public Object fcall(String name, List<String> keys, List<String> args) {
        return jedis.fcall(name, keys, args);
    }

    @Override
    public Object fcallReadonly(String name, List<String> keys, List<String> args) {
        return jedis.fcallReadonly(name, keys, args);
    }

    @Override
    public String functionDelete(String libraryName) {
        return jedis.functionDelete(libraryName);
    }

    @Override
    public String functionLoad(String functionCode) {
        return jedis.functionLoad(functionCode);
    }

    @Override
    public String functionLoadReplace(String functionCode) {
        return jedis.functionLoadReplace(functionCode);
    }

    @Override
    public String functionFlush() {
        return jedis.functionFlush();
    }

    @Override
    public String functionKill() {
        return jedis.functionKill();
    }

    @Override
    public long geoadd(String key, double longitude, double latitude, String member) {
        return jedis.geoadd(key, longitude, latitude, member);
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        return jedis.geodist(key, member1, member2);
    }

    @Override
    public List<String> geohash(String key, String... members) {
        return jedis.geohash(key, members);
    }

    @Override
    public String moduleLoad(String path) {
        return jedis.moduleLoad(path);
    }

    @Override
    public String moduleLoad(String path, String... args) {
        return jedis.moduleLoad(path, args);
    }

    @Override
    public String moduleUnload(String name) {
        return jedis.moduleUnload(name);
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        return jedis.bitfield(key, arguments);
    }

    @Override
    public List<Long> bitfieldReadonly(String key, String... arguments) {
        return jedis.bitfieldReadonly(key, arguments);
    }

    @Override
    public long hstrlen(String key, String field) {
        return jedis.hstrlen(key, field);
    }

    @Override
    public List<Long> hexpire(String key, long seconds, String... fields) {
        return jedis.hexpire(key, seconds, fields);
    }

    @Override
    public List<Long> hpexpire(String key, long milliseconds, String... fields) {
        return jedis.hpexpire(key, milliseconds, fields);
    }

    @Override
    public List<Long> hexpireAt(String key, long unixTimeSeconds, String... fields) {
        return jedis.hexpireAt(key, unixTimeSeconds, fields);
    }

    @Override
    public List<Long> hpexpireAt(String key, long unixTimeMillis, String... fields) {
        return jedis.hpexpireAt(key, unixTimeMillis, fields);
    }

    @Override
    public List<Long> hexpireTime(String key, String... fields) {
        return jedis.hexpireTime(key, fields);
    }

    @Override
    public List<Long> hpexpireTime(String key, String... fields) {
        return jedis.hpexpireTime(key, fields);
    }

    @Override
    public List<Long> httl(String key, String... fields) {
        return jedis.httl(key, fields);
    }

    @Override
    public List<Long> hpttl(String key, String... fields) {
        return jedis.hpttl(key, fields);
    }

    @Override
    public List<Long> hpersist(String key, String... fields) {
        return jedis.hpersist(key, fields);
    }

    @Override
    public String memoryDoctor() {
        return jedis.memoryDoctor();
    }

    @Override
    public Long memoryUsage(String key) {
        return jedis.memoryUsage(key);
    }

    @Override
    public Long memoryUsage(String key, int samples) {
        return jedis.memoryUsage(key, samples);
    }

    @Override
    public String memoryPurge() {
        return jedis.memoryPurge();
    }

    @Override
    public Map<String, Object> memoryStats() {
        return jedis.memoryStats();
    }

    @Override
    public String lolwut() {
        return jedis.lolwut();
    }

    @Override
    public String reset() {
        return jedis.reset();
    }

    @Override
    public String latencyDoctor() {
        return jedis.latencyDoctor();
    }

    @Override
    public long xlen(String key) {
        return jedis.xlen(key);
    }

    @Override
    public long xgroupDestroy(String key, String groupName) {
        return jedis.xgroupDestroy(key, groupName);
    }

    @Override
    public boolean xgroupCreateConsumer(String key, String groupName, String consumerName) {
        return jedis.xgroupCreateConsumer(key, groupName, consumerName);
    }

    @Override
    public long xgroupDelConsumer(String key, String groupName, String consumerName) {
        return jedis.xgroupDelConsumer(key, groupName, consumerName);
    }

    @Override
    public long xtrim(String key, long maxLen, boolean approximateLength) {
        return jedis.xtrim(key, maxLen, approximateLength);
    }

    @Override
    public Object fcall(byte[] name, List<byte[]> keys, List<byte[]> args) {
        return jedis.fcall(name, keys, args);
    }

    @Override
    public Object fcallReadonly(byte[] name, List<byte[]> keys, List<byte[]> args) {
        return jedis.fcallReadonly(name, keys, args);
    }

    @Override
    public String functionDelete(byte[] libraryName) {
        return jedis.functionDelete(libraryName);
    }

    @Override
    public byte[] functionDump() {
        return jedis.functionDump();
    }

    @Override
    public List<Object> functionListBinary() {
        return jedis.functionListBinary();
    }

    @Override
    public List<Object> functionList(byte[] libraryNamePattern) {
        return jedis.functionList(libraryNamePattern);
    }

    @Override
    public List<Object> functionListWithCodeBinary() {
        return jedis.functionListWithCodeBinary();
    }

    @Override
    public List<Object> functionListWithCode(byte[] libraryNamePattern) {
        return jedis.functionListWithCode(libraryNamePattern);
    }

    @Override
    public String functionLoad(byte[] functionCode) {
        return jedis.functionLoad(functionCode);
    }

    @Override
    public String functionLoadReplace(byte[] functionCode) {
        return jedis.functionLoadReplace(functionCode);
    }

    @Override
    public String functionRestore(byte[] serializedValue) {
        return jedis.functionRestore(serializedValue);
    }

    @Override
    public Object functionStatsBinary() {
        return jedis.functionStatsBinary();
    }
}
