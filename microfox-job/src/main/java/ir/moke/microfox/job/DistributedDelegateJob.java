package ir.moke.microfox.job;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.redis.cluster.ClusterLock;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributedDelegateJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(DistributedDelegateJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        JobKey key = context.getJobDetail().getKey();

        boolean allowConcurrent = dataMap.getBoolean("allowConcurrent");
        String identity = dataMap.getString("identity");

        String group = key.getGroup();
        String name = key.getName();

        String jobKey = "quartz:job:distributed:%s:%s".formatted(group, name);
        boolean acquire = false;

        if (identity == null || identity.isBlank()) {
            logger.error("Distributed job {} has no identity configured", jobKey);
            return;
        }

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
    }
}
