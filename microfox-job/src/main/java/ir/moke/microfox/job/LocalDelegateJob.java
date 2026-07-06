package ir.moke.microfox.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LocalDelegateJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(LocalDelegateJob.class);
    private static final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        JobKey key = context.getJobDetail().getKey();

        boolean allowConcurrent = dataMap.getBoolean("allowConcurrent");
        String type = dataMap.getString("type");

        String group = key.getGroup();
        String name = key.getName();

        String jobKey = "quartz:distributed:%s:%s:%s".formatted(type, group, name);

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
