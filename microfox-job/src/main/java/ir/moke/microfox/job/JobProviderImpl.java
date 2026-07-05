package ir.moke.microfox.job;

import ir.moke.microfox.api.job.JobProvider;
import ir.moke.microfox.api.job.Task;
import ir.moke.microfox.exception.MicroFoxException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import static org.quartz.CronScheduleBuilder.cronSchedule;

public class JobProviderImpl implements JobProvider {
    private final Scheduler scheduler;

    private static final Function<String, String> DEFAULT_JOB_NAME = g -> Optional.ofNullable(g).orElse("MICROFOX_JOB");

    public JobProviderImpl() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(Task task, String name, String group, String cronExpression, boolean concurrentExecution) {
        try {
            JobKey jobKey = new JobKey(name, DEFAULT_JOB_NAME.apply(group));
            if (isJobExists(jobKey)) throw new MicroFoxException("The job with same name and group already exists");
            TaskRegistry.register(jobKey, task);
            JobDetail job = JobBuilder.newJob(DelegateJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("concurrentExecution", concurrentExecution)
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

    private void register(Task task, String name, String group, ZonedDateTime zonedDateTime) {
        try {
            JobKey jobKey = new JobKey(name, DEFAULT_JOB_NAME.apply(group));
            if (isJobExists(jobKey)) throw new MicroFoxException("The job with same name and group already exists");
            TaskRegistry.register(jobKey, task);
            JobDetail job = JobBuilder.newJob(DelegateJob.class)
                    .withIdentity(jobKey)
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .startAt(Date.from(zonedDateTime.toInstant()))
                    .build();

            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void job(Task task, String name, String group, String cronExpression, boolean concurrentExecution) {
        register(task, name, group, cronExpression, concurrentExecution);
    }

    @Override
    public void job(Task task, String name, String group, ZonedDateTime zonedDateTime) {
        register(task, name, group, zonedDateTime);
    }

    @Override
    public void jobTrigger(String name, String group) {
        try {
            scheduler.triggerJob(JobKey.jobKey(name, group));
        } catch (SchedulerException e) {
            throw new MicroFoxException(e);
        }
    }

    @Override
    public void pauseJob(String name, String group) {
        try {
            scheduler.pauseJob(new JobKey(name, DEFAULT_JOB_NAME.apply(group)));
        } catch (SchedulerException e) {
            throw new MicroFoxException(e);
        }
    }

    @Override
    public void resumeJob(String name, String group) {
        try {
            scheduler.resumeJob(new JobKey(name, DEFAULT_JOB_NAME.apply(group)));
        } catch (SchedulerException e) {
            throw new MicroFoxException(e);
        }
    }

    @Override
    public void resumeAllJob() {
        try {
            scheduler.resumeAll();
            TaskRegistry.removeAll();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pauseAllJob() {
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            throw new MicroFoxException(e);
        }
    }

    @Override
    public void deleteJob(String name, String group) {
        try {
            JobKey jobKey = new JobKey(name, group);
            scheduler.deleteJob(jobKey);
            TaskRegistry.remove(jobKey);

        } catch (SchedulerException e) {
            throw new MicroFoxException(e);
        }
    }

    private boolean isJobExists(JobKey jobKey) {
        try {
            return scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            throw new MicroFoxException(e);
        }
    }
}
