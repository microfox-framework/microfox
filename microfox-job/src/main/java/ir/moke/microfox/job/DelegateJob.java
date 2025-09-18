package ir.moke.microfox.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
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
        boolean disableConcurrentExecution = dataMap.getBoolean("disableConcurrentExecution");
        ReentrantLock lock = null;
        if (disableConcurrentExecution) {
            String jobKey = context.getJobDetail().getKey().toString();
            lock = locks.computeIfAbsent(jobKey, k -> new ReentrantLock());

            if (!lock.tryLock()) {
                logger.debug("Job {} is already running, skipping...", jobKey);
                return;
            }
        }

        try {
            Runnable task = (Runnable) dataMap.get("task");
            if (task != null) task.run();
        } finally {
            if (disableConcurrentExecution) lock.unlock();
        }
    }
}
