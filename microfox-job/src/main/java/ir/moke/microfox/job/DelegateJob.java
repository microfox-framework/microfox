package ir.moke.microfox.job;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.redis.cluster.ClusterLock;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class DelegateJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(DelegateJob.class);
    private static final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        JobKey key = context.getJobDetail().getKey();

        boolean allowConcurrent = dataMap.getBoolean("allowConcurrent");
        boolean distribute = dataMap.getBoolean("distribute");
        String identity = dataMap.getString("identity");

        String group = key.getGroup();
        String name = key.getName();

        String jobKey = "QUARTZ:JOB:%s:%s".formatted(group, name);
        boolean acquire = false;

        if (distribute && (identity == null || identity.isBlank())) {
            logger.error("Distributed job {} has no identity configured", jobKey);
            return;
        }

        if (distribute) {
            if (!allowConcurrent) {
                ClusterLock lock = MicroFox.redisCluster(identity).getLock(jobKey);
                try {
                    acquire = lock.tryLock(0);
                    if (!acquire) {
                        logger.debug("Job {} is already running, skipping...", jobKey);
                        return;
                    }

                    TaskRegistry.get(key).run();
                } catch (Exception e) {
                    logger.error("Job {} failed", jobKey, e);
                } finally {
                    if (acquire && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                TaskRegistry.get(key).run();
            }
        } else {
            if (!allowConcurrent) {
                ReentrantLock lock = locks.computeIfAbsent(jobKey, k -> new ReentrantLock());

                if (!lock.tryLock()) {
                    logger.debug("Local job {} is already running, skipping...", jobKey);
                    return;
                }

                try {
                    TaskRegistry.get(key).run();
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        locks.remove(jobKey, lock);
                    }
                }
            } else {
                TaskRegistry.get(key).run();
            }
        }
    }
}
