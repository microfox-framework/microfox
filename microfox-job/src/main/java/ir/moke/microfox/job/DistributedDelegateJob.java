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
        String type = dataMap.getString("type");

        String group = key.getGroup();
        String name = key.getName();

        String jobKey = "quartz:distributed:%s:%s:%s".formatted(type, group, name);

        if (identity == null || identity.isBlank()) {
            logger.error("Distributed job {} has no identity configured", jobKey);
            return;
        }

        if (!allowConcurrent) {
            ClusterLock lock = MicroFox.redisCluster(identity).getLock(jobKey);
            try {
                if (lock.isLocked()) {
                    logger.debug("Job {} is already running, skipping...", jobKey);
                    return;
                }
                lock.lock();
                TaskRegistry.get(key).run();
            } catch (Exception e) {
                logger.error("Job {} failed", jobKey, e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            TaskRegistry.get(key).run();
        }
    }
}
