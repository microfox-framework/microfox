package ir.moke.microfox.job;

import ir.moke.microfox.api.job.JobProvider;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.quartz.CronScheduleBuilder.cronSchedule;

public class JobProviderImpl implements JobProvider {
    private final Scheduler scheduler;

    public JobProviderImpl() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(Runnable task, String cronExpression, boolean disableConcurrentExecution) {
        try {
            JobDetail job = JobBuilder.newJob(DelegateJob.class)
                    .withIdentity("job-" + UUID.randomUUID())
                    .usingJobData("disableConcurrentExecution", disableConcurrentExecution)
                    .usingJobData(new JobDataMap(Map.of("task", task)))
                    .build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(cronSchedule(cronExpression))
                    .build();

            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void register(Runnable task, Date startAt) {
        try {
            JobDetail job = JobBuilder.newJob(DelegateJob.class)
                    .withIdentity("job-" + UUID.randomUUID())
                    .usingJobData(new JobDataMap(Map.of("task", task)))
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .startAt(startAt)
                    .build();

            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void job(Runnable task, String cronExpression, boolean disableConcurrentExecution) {
        register(task, cronExpression, disableConcurrentExecution);
    }

    @Override
    public void job(Runnable task, ZonedDateTime zonedDateTime) {
        register(task, Date.from(zonedDateTime.toInstant()));
    }
}
