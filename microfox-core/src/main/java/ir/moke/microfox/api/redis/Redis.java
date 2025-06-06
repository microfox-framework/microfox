package ir.moke.microfox.api.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Redis {
    String toString();

    void connect();

    void disconnect();

    boolean isConnected();

    boolean isBroken();

    void resetState();

    void close();

    RedisTransaction multi();

    int getDB();

    String ping();

    byte[] ping(byte[] message);

    String select(int index);

    String swapDB(int index1, int index2);

    String flushDB();

    String flushAll();

    boolean copy(byte[] srcKey, byte[] dstKey, int db, boolean replace);

    boolean copy(byte[] srcKey, byte[] dstKey, boolean replace);

    String set(byte[] key, byte[] value);

    byte[] get(byte[] key);

    byte[] setGet(byte[] key, byte[] value);

    byte[] getDel(byte[] key);

    long exists(byte[]... keys);

    boolean exists(byte[] key);

    long del(byte[]... keys);

    long del(byte[] key);

    long unlink(byte[]... keys);

    long unlink(byte[] key);

    String type(byte[] key);

    Set<byte[]> keys(byte[] pattern);

    byte[] randomBinaryKey();

    String rename(byte[] oldkey, byte[] newkey);

    long renamenx(byte[] oldkey, byte[] newkey);

    long dbSize();

    long expire(byte[] key, long seconds);

    long pexpire(byte[] key, long milliseconds);

    long expireTime(byte[] key);

    long pexpireTime(byte[] key);

    long expireAt(byte[] key, long unixTime);

    long pexpireAt(byte[] key, long millisecondsTimestamp);

    long ttl(byte[] key);

    long touch(byte[]... keys);

    long touch(byte[] key);

    long move(byte[] key, int dbIndex);

    List<byte[]> mget(byte[]... keys);

    long setnx(byte[] key, byte[] value);

    String setex(byte[] key, long seconds, byte[] value);

    String mset(byte[]... keysvalues);

    long msetnx(byte[]... keysvalues);

    long decrBy(byte[] key, long decrement);

    long decr(byte[] key);

    long incrBy(byte[] key, long increment);

    double incrByFloat(byte[] key, double increment);

    long incr(byte[] key);

    long append(byte[] key, byte[] value);

    byte[] substr(byte[] key, int start, int end);

    long hset(byte[] key, byte[] field, byte[] value);

    long hset(byte[] key, Map<byte[], byte[]> hash);

    byte[] hget(byte[] key, byte[] field);

    List<byte[]> hgetdel(byte[] key, byte[]... fields);

    long hsetnx(byte[] key, byte[] field, byte[] value);

    String hmset(byte[] key, Map<byte[], byte[]> hash);

    List<byte[]> hmget(byte[] key, byte[]... fields);

    long hincrBy(byte[] key, byte[] field, long value);

    double hincrByFloat(byte[] key, byte[] field, double value);

    boolean hexists(byte[] key, byte[] field);

    long hdel(byte[] key, byte[]... fields);

    long hlen(byte[] key);

    Set<byte[]> hkeys(byte[] key);

    List<byte[]> hvals(byte[] key);

    Map<byte[], byte[]> hgetAll(byte[] key);

    byte[] hrandfield(byte[] key);

    List<byte[]> hrandfield(byte[] key, long count);

    List<Map.Entry<byte[], byte[]>> hrandfieldWithValues(byte[] key, long count);

    long rpush(byte[] key, byte[]... strings);

    long lpush(byte[] key, byte[]... strings);

    long llen(byte[] key);

    List<byte[]> lrange(byte[] key, long start, long stop);

    String ltrim(byte[] key, long start, long stop);

    byte[] lindex(byte[] key, long index);

    String lset(byte[] key, long index, byte[] value);

    long lrem(byte[] key, long count, byte[] value);

    byte[] lpop(byte[] key);

    List<byte[]> lpop(byte[] key, int count);

    Long lpos(byte[] key, byte[] element);

    byte[] rpop(byte[] key);

    List<byte[]> rpop(byte[] key, int count);

    byte[] rpoplpush(byte[] srckey, byte[] dstkey);

    long sadd(byte[] key, byte[]... members);

    Set<byte[]> smembers(byte[] key);

    long srem(byte[] key, byte[]... members);

    byte[] spop(byte[] key);

    Set<byte[]> spop(byte[] key, long count);

    long smove(byte[] srckey, byte[] dstkey, byte[] member);

    long scard(byte[] key);

    boolean sismember(byte[] key, byte[] member);

    List<Boolean> smismember(byte[] key, byte[]... members);

    Set<byte[]> sinter(byte[]... keys);

    long sinterstore(byte[] dstkey, byte[]... keys);

    long sintercard(byte[]... keys);

    long sintercard(int limit, byte[]... keys);

    Set<byte[]> sunion(byte[]... keys);

    long sunionstore(byte[] dstkey, byte[]... keys);

    Set<byte[]> sdiff(byte[]... keys);

    long sdiffstore(byte[] dstkey, byte[]... keys);

    byte[] srandmember(byte[] key);

    List<byte[]> srandmember(byte[] key, int count);

    long zadd(byte[] key, double score, byte[] member);

    long zadd(byte[] key, Map<byte[], Double> scoreMembers);

    List<byte[]> zrange(byte[] key, long start, long stop);

    long zrem(byte[] key, byte[]... members);

    double zincrby(byte[] key, double increment, byte[] member);

    Long zrank(byte[] key, byte[] member);

    Long zrevrank(byte[] key, byte[] member);

    List<byte[]> zrevrange(byte[] key, long start, long stop);

    byte[] zrandmember(byte[] key);

    List<byte[]> zrandmember(byte[] key, long count);

    long zcard(byte[] key);

    Double zscore(byte[] key, byte[] member);

    List<Double> zmscore(byte[] key, byte[]... members);

    String watch(byte[]... keys);

    String unwatch();

    List<byte[]> sort(byte[] key);

    long sort(byte[] key, byte[] dstkey);

    List<byte[]> blpop(int timeout, byte[]... keys);

    List<byte[]> brpop(int timeout, byte[]... keys);

    String auth(String password);

    String auth(String user, String password);

    long zcount(byte[] key, double min, double max);

    long zcount(byte[] key, byte[] min, byte[] max);

    List<byte[]> zdiff(byte[]... keys);

    long zdiffstore(byte[] dstkey, byte[]... keys);

    List<byte[]> zrangeByScore(byte[] key, double min, double max);

    List<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max);

    List<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count);

    List<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max, int offset, int count);

    List<byte[]> zrevrangeByScore(byte[] key, double max, double min);

    List<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min);

    List<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count);

    List<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min, int offset, int count);

    long zremrangeByRank(byte[] key, long start, long stop);

    long zremrangeByScore(byte[] key, double min, double max);

    long zremrangeByScore(byte[] key, byte[] min, byte[] max);

    long zunionstore(byte[] dstkey, byte[]... sets);

    long zinterstore(byte[] dstkey, byte[]... sets);

    long zintercard(byte[]... keys);

    long zintercard(long limit, byte[]... keys);

    long zlexcount(byte[] key, byte[] min, byte[] max);

    List<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max);

    List<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max, int offset, int count);

    List<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min);

    List<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min, int offset, int count);

    long zremrangeByLex(byte[] key, byte[] min, byte[] max);

    String save();

    String bgsave();

    String bgsaveSchedule();

    String bgrewriteaof();

    long lastsave();

    String shutdownAbort();

    String info();

    String info(String section);

    String replicaof(String host, int port);

    String replicaofNoOne();

    List<Object> roleBinary();

    Map<byte[], byte[]> configGet(byte[] pattern);

    Map<byte[], byte[]> configGet(byte[]... patterns);

    String configResetStat();

    String configRewrite();

    String configSet(byte[] parameter, byte[] value);

    String configSet(byte[]... parameterValues);

    String configSetBinary(Map<byte[], byte[]> parameterValues);

    long strlen(byte[] key);

    long lpushx(byte[] key, byte[]... strings);

    long persist(byte[] key);

    long rpushx(byte[] key, byte[]... strings);

    byte[] echo(byte[] string);

    byte[] brpoplpush(byte[] source, byte[] destination, int timeout);

    boolean setbit(byte[] key, long offset, boolean value);

    boolean getbit(byte[] key, long offset);

    long bitpos(byte[] key, boolean value);

    long setrange(byte[] key, long offset, byte[] value);

    byte[] getrange(byte[] key, long startOffset, long endOffset);

    long publish(byte[] channel, byte[] message);

    Object eval(byte[] script, List<byte[]> keys, List<byte[]> args);

    Object evalReadonly(byte[] script, List<byte[]> keys, List<byte[]> args);

    Object eval(byte[] script, int keyCount, byte[]... params);

    Object eval(byte[] script);

    Object evalsha(byte[] sha1);

    Object evalsha(byte[] sha1, List<byte[]> keys, List<byte[]> args);

    Object evalshaReadonly(byte[] sha1, List<byte[]> keys, List<byte[]> args);

    Object evalsha(byte[] sha1, int keyCount, byte[]... params);

    String scriptFlush();

    Boolean scriptExists(byte[] sha1);

    List<Boolean> scriptExists(byte[]... sha1);

    byte[] scriptLoad(byte[] script);

    String scriptKill();

    String slowlogReset();

    long slowlogLen();

    List<Object> slowlogGetBinary();

    List<Object> slowlogGetBinary(long entries);

    Long objectRefcount(byte[] key);

    byte[] objectEncoding(byte[] key);

    Long objectIdletime(byte[] key);

    List<byte[]> objectHelpBinary();

    Long objectFreq(byte[] key);

    long bitcount(byte[] key);

    long bitcount(byte[] key, long start, long end);

    byte[] dump(byte[] key);

    String restore(byte[] key, long ttl, byte[] serializedValue);

    long pttl(byte[] key);

    String psetex(byte[] key, long milliseconds, byte[] value);

    byte[] memoryDoctorBinary();

    Long memoryUsage(byte[] key);

    Long memoryUsage(byte[] key, int samples);

    String failover();

    String failoverAbort();

    byte[] aclWhoAmIBinary();

    byte[] aclGenPassBinary();

    byte[] aclGenPassBinary(int bits);

    List<byte[]> aclListBinary();

    List<byte[]> aclUsersBinary();

    String aclSetUser(byte[] name);

    String aclSetUser(byte[] name, byte[]... rules);

    long aclDelUser(byte[]... names);

    List<byte[]> aclCatBinary();

    List<byte[]> aclCat(byte[] category);

    List<byte[]> aclLogBinary();

    List<byte[]> aclLogBinary(int limit);

    String aclLogReset();

    String clientKill(byte[] ipPort);

    String clientKill(String ip, int port);

    byte[] clientGetnameBinary();

    byte[] clientListBinary();

    byte[] clientListBinary(long... clientIds);


    byte[] clientInfoBinary();

    String clientSetname(byte[] name);

    long clientId();

    long clientUnblock(long clientId);

    String clientPause(long timeout);

    String clientUnpause();

    String clientNoEvictOn();

    String clientNoEvictOff();

    String clientNoTouchOn();

    String clientNoTouchOff();

    List<String> time();

    String migrate(String host, int port, byte[] key, int destinationDb, int timeout);

    String migrate(String host, int port, byte[] key, int timeout);

    long waitReplicas(int replicas, long timeout);

    long pfadd(byte[] key, byte[]... elements);

    long pfcount(byte[] key);

    String pfmerge(byte[] destkey, byte[]... sourcekeys);

    long pfcount(byte[]... keys);

    long geoadd(byte[] key, double longitude, double latitude, byte[] member);

    Double geodist(byte[] key, byte[] member1, byte[] member2);

    List<byte[]> geohash(byte[] key, byte[]... members);

    List<Long> bitfield(byte[] key, byte[]... arguments);

    List<Long> bitfieldReadonly(byte[] key, byte[]... arguments);

    long hstrlen(byte[] key, byte[] field);

    List<Long> hexpire(byte[] key, long seconds, byte[]... fields);

    List<Long> hpexpire(byte[] key, long milliseconds, byte[]... fields);

    List<Long> hexpireAt(byte[] key, long unixTimeSeconds, byte[]... fields);

    List<Long> hpexpireAt(byte[] key, long unixTimeMillis, byte[]... fields);

    List<Long> hexpireTime(byte[] key, byte[]... fields);

    List<Long> hpexpireTime(byte[] key, byte[]... fields);

    List<Long> httl(byte[] key, byte[]... fields);

    List<Long> hpttl(byte[] key, byte[]... fields);

    List<Long> hpersist(byte[] key, byte[]... fields);

    long xlen(byte[] key);

    List<Object> xrange(byte[] key, byte[] start, byte[] end);

    List<Object> xrange(byte[] key, byte[] start, byte[] end, int count);

    List<Object> xrevrange(byte[] key, byte[] end, byte[] start);

    List<Object> xrevrange(byte[] key, byte[] end, byte[] start, int count);

    long xack(byte[] key, byte[] group, byte[]... ids);

    String xgroupCreate(byte[] key, byte[] consumer, byte[] id, boolean makeStream);

    String xgroupSetID(byte[] key, byte[] consumer, byte[] id);

    long xgroupDestroy(byte[] key, byte[] consumer);

    boolean xgroupCreateConsumer(byte[] key, byte[] groupName, byte[] consumerName);

    long xgroupDelConsumer(byte[] key, byte[] groupName, byte[] consumerName);

    long xdel(byte[] key, byte[]... ids);

    long xtrim(byte[] key, long maxLen, boolean approximateLength);

    Object xpending(byte[] key, byte[] groupName);

    Object xinfoStream(byte[] key);

    Object xinfoStreamFull(byte[] key);

    Object xinfoStreamFull(byte[] key, int count);

    List<Object> xinfoGroups(byte[] key);

    List<Object> xinfoConsumers(byte[] key, byte[] group);

    boolean copy(String srcKey, String dstKey, int db, boolean replace);

    boolean copy(String srcKey, String dstKey, boolean replace);

    String ping(String message);

    String set(String key, String value);

    String get(String key);

    String setGet(String key, String value);

    String getDel(String key);

    long exists(String... keys);

    boolean exists(String key);

    long del(String... keys);

    long del(String key);

    long unlink(String... keys);

    long unlink(String key);

    String type(String key);

    Set<String> keys(String pattern);

    String randomKey();

    String rename(String oldkey, String newkey);

    long renamenx(String oldkey, String newkey);

    long expire(String key, long seconds);

    long pexpire(String key, long milliseconds);

    long expireTime(String key);

    long pexpireTime(String key);

    long expireAt(String key, long unixTime);

    long pexpireAt(String key, long millisecondsTimestamp);

    long ttl(String key);

    long touch(String... keys);

    long touch(String key);

    long move(String key, int dbIndex);

    List<String> mget(String... keys);

    long setnx(String key, String value);

    String setex(String key, long seconds, String value);

    String mset(String... keysvalues);

    long msetnx(String... keysvalues);

    long decrBy(String key, long decrement);

    long decr(String key);

    long incrBy(String key, long increment);

    double incrByFloat(String key, double increment);

    long incr(String key);

    long append(String key, String value);

    String substr(String key, int start, int end);

    long hset(String key, String field, String value);

    long hset(String key, Map<String, String> hash);

    String hget(String key, String field);

    List<String> hgetdel(String key, String... fields);

    long hsetnx(String key, String field, String value);

    String hmset(String key, Map<String, String> hash);

    List<String> hmget(String key, String... fields);

    long hincrBy(String key, String field, long value);

    double hincrByFloat(String key, String field, double value);

    boolean hexists(String key, String field);

    long hdel(String key, String... fields);

    long hlen(String key);

    Set<String> hkeys(String key);

    List<String> hvals(String key);

    Map<String, String> hgetAll(String key);

    String hrandfield(String key);

    List<String> hrandfield(String key, long count);

    List<Map.Entry<String, String>> hrandfieldWithValues(String key, long count);

    long rpush(String key, String... strings);

    long lpush(String key, String... strings);

    long llen(String key);

    List<String> lrange(String key, long start, long stop);

    String ltrim(String key, long start, long stop);

    String lindex(String key, long index);

    String lset(String key, long index, String value);

    long lrem(String key, long count, String value);

    String lpop(String key);

    List<String> lpop(String key, int count);

    Long lpos(String key, String element);

    String rpop(String key);

    List<String> rpop(String key, int count);

    String rpoplpush(String srckey, String dstkey);

    long sadd(String key, String... members);

    Set<String> smembers(String key);

    long srem(String key, String... members);

    String spop(String key);

    Set<String> spop(String key, long count);

    long smove(String srckey, String dstkey, String member);

    long scard(String key);

    boolean sismember(String key, String member);

    List<Boolean> smismember(String key, String... members);

    Set<String> sinter(String... keys);

    long sinterstore(String dstkey, String... keys);

    long sintercard(String... keys);

    long sintercard(int limit, String... keys);

    Set<String> sunion(String... keys);

    long sunionstore(String dstkey, String... keys);

    Set<String> sdiff(String... keys);

    long sdiffstore(String dstkey, String... keys);

    String srandmember(String key);

    List<String> srandmember(String key, int count);

    long zadd(String key, double score, String member);

    long zadd(String key, Map<String, Double> scoreMembers);

    List<String> zdiff(String... keys);

    long zdiffstore(String dstkey, String... keys);

    List<String> zrange(String key, long start, long stop);

    long zrem(String key, String... members);

    double zincrby(String key, double increment, String member);

    Long zrank(String key, String member);

    Long zrevrank(String key, String member);

    List<String> zrevrange(String key, long start, long stop);

    String zrandmember(String key);

    List<String> zrandmember(String key, long count);

    long zcard(String key);

    Double zscore(String key, String member);

    List<Double> zmscore(String key, String... members);

    String watch(String... keys);

    List<String> sort(String key);

    long sort(String key, String dstkey);

    List<String> blpop(int timeout, String... keys);

    List<String> brpop(int timeout, String... keys);

    List<String> blpop(int timeout, String key);

    List<String> brpop(int timeout, String key);

    long zcount(String key, double min, double max);

    long zcount(String key, String min, String max);

    List<String> zrangeByScore(String key, double min, double max);

    List<String> zrangeByScore(String key, String min, String max);

    List<String> zrangeByScore(String key, double min, double max, int offset, int count);

    List<String> zrangeByScore(String key, String min, String max, int offset, int count);

    List<String> zrevrangeByScore(String key, double max, double min);

    List<String> zrevrangeByScore(String key, String max, String min);

    List<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

    List<String> zrevrangeByScore(String key, String max, String min, int offset, int count);

    long zremrangeByRank(String key, long start, long stop);

    long zremrangeByScore(String key, double min, double max);

    long zremrangeByScore(String key, String min, String max);

    long zunionstore(String dstkey, String... sets);

    long zintercard(String... keys);

    long zintercard(long limit, String... keys);

    long zinterstore(String dstkey, String... sets);

    long zlexcount(String key, String min, String max);

    List<String> zrangeByLex(String key, String min, String max);

    List<String> zrangeByLex(String key, String min, String max, int offset, int count);

    List<String> zrevrangeByLex(String key, String max, String min);

    List<String> zrevrangeByLex(String key, String max, String min, int offset, int count);

    long zremrangeByLex(String key, String min, String max);

    long strlen(String key);

    long lpushx(String key, String... strings);

    long persist(String key);

    long rpushx(String key, String... strings);

    String echo(String string);

    String brpoplpush(String source, String destination, int timeout);

    boolean setbit(String key, long offset, boolean value);

    boolean getbit(String key, long offset);

    long setrange(String key, long offset, String value);

    String getrange(String key, long startOffset, long endOffset);

    long bitpos(String key, boolean value);

    List<Object> role();

    Map<String, String> configGet(String pattern);

    Map<String, String> configGet(String... patterns);

    String configSet(String parameter, String value);

    String configSet(String... parameterValues);

    String configSet(Map<String, String> parameterValues);

    long publish(String channel, String message);

    List<String> pubsubChannels();

    List<String> pubsubChannels(String pattern);

    Long pubsubNumPat();

    Map<String, Long> pubsubNumSub(String... channels);

    List<String> pubsubShardChannels();

    List<String> pubsubShardChannels(String pattern);

    Map<String, Long> pubsubShardNumSub(String... channels);

    Object eval(String script, int keyCount, String... params);

    Object eval(String script, List<String> keys, List<String> args);

    Object evalReadonly(String script, List<String> keys, List<String> args);

    Object eval(String script);

    Object evalsha(String sha1);

    Object evalsha(String sha1, List<String> keys, List<String> args);

    Object evalshaReadonly(String sha1, List<String> keys, List<String> args);

    Object evalsha(String sha1, int keyCount, String... params);

    Boolean scriptExists(String sha1);

    List<Boolean> scriptExists(String... sha1);

    String scriptLoad(String script);

    Long objectRefcount(String key);

    String objectEncoding(String key);

    Long objectIdletime(String key);

    List<String> objectHelp();

    Long objectFreq(String key);

    long bitcount(String key);

    long bitcount(String key, long start, long end);


    long commandCount();

    List<String> commandGetKeys(String... command);

    List<String> commandList();

    String sentinelMyId();

    List<Map<String, String>> sentinelMasters();

    Map<String, String> sentinelMaster(String masterName);

    List<Map<String, String>> sentinelSentinels(String masterName);

    List<String> sentinelGetMasterAddrByName(String masterName);

    Long sentinelReset(String pattern);

    List<Map<String, String>> sentinelReplicas(String masterName);

    String sentinelFailover(String masterName);

    String sentinelMonitor(String masterName, String ip, int port, int quorum);

    String sentinelRemove(String masterName);

    String sentinelSet(String masterName, Map<String, String> parameterMap);

    byte[] dump(String key);

    String restore(String key, long ttl, byte[] serializedValue);

    long pttl(String key);

    String psetex(String key, long milliseconds, String value);

    String aclSetUser(String name);

    String aclSetUser(String name, String... rules);

    long aclDelUser(String... names);

    List<String> aclUsers();

    List<String> aclList();

    String aclWhoAmI();

    List<String> aclCat();

    List<String> aclCat(String category);

    String aclLoad();

    String aclSave();

    String aclGenPass();

    String aclGenPass(int bits);

    String aclDryRun(String username, String command, String... args);

    byte[] aclDryRunBinary(byte[] username, byte[] command, byte[]... args);

    String clientKill(String ipPort);

    String clientGetname();

    String clientList();

    String clientList(long... clientIds);

    String clientInfo();

    String clientSetname(String name);

    String migrate(String host, int port, String key, int destinationDb, int timeout);

    String migrate(String host, int port, String key, int timeout);

    String readonly();

    String readwrite();

    String clusterNodes();

    String clusterMeet(String ip, int port);

    String clusterReset();

    String clusterAddSlots(int... slots);

    String clusterDelSlots(int... slots);

    String clusterInfo();

    List<String> clusterGetKeysInSlot(int slot, int count);

    List<byte[]> clusterGetKeysInSlotBinary(int slot, int count);

    String clusterSetSlotNode(int slot, String nodeId);

    String clusterSetSlotMigrating(int slot, String nodeId);

    String clusterSetSlotImporting(int slot, String nodeId);

    String clusterSetSlotStable(int slot);

    String clusterForget(String nodeId);

    String clusterFlushSlots();

    long clusterKeySlot(String key);

    long clusterCountFailureReports(String nodeId);

    long clusterCountKeysInSlot(int slot);

    String clusterSaveConfig();

    String clusterSetConfigEpoch(long configEpoch);

    String clusterBumpEpoch();

    String clusterReplicate(String nodeId);

    List<String> clusterReplicas(String nodeId);

    String clusterFailover();

    String clusterMyId();

    String clusterMyShardId();

    List<Map<String, Object>> clusterLinks();

    String clusterAddSlotsRange(int... ranges);

    String clusterDelSlotsRange(int... ranges);

    String asking();

    long pfadd(String key, String... elements);

    long pfcount(String key);

    long pfcount(String... keys);

    String pfmerge(String destkey, String... sourcekeys);

    Object fcall(String name, List<String> keys, List<String> args);

    Object fcallReadonly(String name, List<String> keys, List<String> args);

    String functionDelete(String libraryName);

    String functionLoad(String functionCode);

    String functionLoadReplace(String functionCode);

    String functionFlush();

    String functionKill();

    long geoadd(String key, double longitude, double latitude, String member);

    Double geodist(String key, String member1, String member2);

    List<String> geohash(String key, String... members);

    String moduleLoad(String path);

    String moduleLoad(String path, String... args);

    String moduleUnload(String name);

    List<Long> bitfield(String key, String... arguments);

    List<Long> bitfieldReadonly(String key, String... arguments);

    long hstrlen(String key, String field);

    List<Long> hexpire(String key, long seconds, String... fields);

    List<Long> hpexpire(String key, long milliseconds, String... fields);

    List<Long> hexpireAt(String key, long unixTimeSeconds, String... fields);

    List<Long> hpexpireAt(String key, long unixTimeMillis, String... fields);

    List<Long> hexpireTime(String key, String... fields);

    List<Long> hpexpireTime(String key, String... fields);

    List<Long> httl(String key, String... fields);

    List<Long> hpttl(String key, String... fields);

    List<Long> hpersist(String key, String... fields);

    String memoryDoctor();

    Long memoryUsage(String key);

    Long memoryUsage(String key, int samples);

    String memoryPurge();

    Map<String, Object> memoryStats();

    String lolwut();

    String reset();

    String latencyDoctor();

    long xlen(String key);

    long xgroupDestroy(String key, String groupName);

    boolean xgroupCreateConsumer(String key, String groupName, String consumerName);

    long xgroupDelConsumer(String key, String groupName, String consumerName);

    long xtrim(String key, long maxLen, boolean approximateLength);

    Object fcall(byte[] name, List<byte[]> keys, List<byte[]> args);

    Object fcallReadonly(byte[] name, List<byte[]> keys, List<byte[]> args);

    String functionDelete(byte[] libraryName);

    byte[] functionDump();

    List<Object> functionListBinary();

    List<Object> functionList(byte[] libraryNamePattern);

    List<Object> functionListWithCodeBinary();

    List<Object> functionListWithCode(byte[] libraryNamePattern);

    String functionLoad(byte[] functionCode);

    String functionLoadReplace(byte[] functionCode);

    String functionRestore(byte[] serializedValue);

    Object functionStatsBinary();

}
