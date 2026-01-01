package ir.moke.microfox.job;

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
        boolean disableConcurrentExecution = dataMap.getBoolean("disableConcurrentExecution");
        ReentrantLock lock = null;
        if (disableConcurrentExecution) {
            String jobKey = key.toString();
            lock = locks.computeIfAbsent(jobKey, k -> new ReentrantLock());

            if (!lock.tryLock()) {
                logger.debug("Job {} is already running, skipping...", jobKey);
                return;
            }
        }

        try {
            TaskRegistry.get(key).run();
        } finally {
            if (disableConcurrentExecution) lock.unlock();
        }
    }
}
