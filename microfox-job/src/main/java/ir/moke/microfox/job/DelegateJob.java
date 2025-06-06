package ir.moke.microfox.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class DelegateJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Runnable task = (Runnable) jobExecutionContext.getMergedJobDataMap().get("task");
        if (task != null) task.run();
    }
}
