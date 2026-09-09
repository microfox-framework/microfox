package ir.moke.microfox.job;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.redis.RedisProvider;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

public class DistributedDelegateJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(DistributedDelegateJob.class);

    private static final RedisProvider redisProvider = ServiceLoader.load(RedisProvider.class).findFirst().orElse(null);

    static {
        if (redisProvider == null) throw new UnsupportedOperationException("redis support not available");
    }

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        JobKey key = context.getJobDetail().getKey();

        boolean allowConcurrent = dataMap.getBoolean("allowConcurrent");
        String identity = dataMap.getString("identity");
        String type = dataMap.getString("type");

        String group = key.getGroup();
        String name = key.getName();

        String jobKey = "microfox:quartz:lock:%s:%s:%s".formatted(type, group, name);

        if (identity == null || identity.isBlank()) {
            logger.error("Distributed job {} has no identity configured", jobKey);
            return;
        }

        if (!allowConcurrent) {
            RedissonClient client = MicroFox.redis(identity);
            RLock lock = client.getLock(jobKey);

            boolean acquired = false;
            try {
                acquired = lock.tryLock(0, TimeUnit.MILLISECONDS);
                if (!acquired) {
                    logger.debug("Job {} is already running, skipping...", jobKey);
                    return;
                }
                TaskRegistry.get(key).run();
            } catch (Exception e) {
                logger.error("Job {} failed", jobKey, e);
            } finally {
                if (acquired && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            TaskRegistry.get(key).run();
        }
    }
}
