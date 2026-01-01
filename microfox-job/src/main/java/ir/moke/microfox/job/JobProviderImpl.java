package ir.moke.microfox.job;

import ir.moke.microfox.api.job.JobProvider;
import ir.moke.microfox.exception.MicrofoxException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.quartz.CronScheduleBuilder.cronSchedule;

public class JobProviderImpl implements JobProvider {
    private static final String DEFAULT_JOB_GROUP_NAME = "MICROFOX_JOB";
    private final Scheduler scheduler;

    public JobProviderImpl() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(Runnable task, String name, String group, String cronExpression, boolean disableConcurrentExecution) {
        try {
            JobKey jobKey = new JobKey(name, Optional.ofNullable(group).orElse(DEFAULT_JOB_GROUP_NAME));
            if (isJobExists(jobKey)) throw new MicrofoxException("The job with same name and group already exists");
            JobDetail job = JobBuilder.newJob(DelegateJob.class)
                    .withIdentity(jobKey)
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

    private void register(Runnable task, String name, String group, Date startAt) {
        try {
            JobKey jobKey = new JobKey(name, Optional.ofNullable(group).orElse(DEFAULT_JOB_GROUP_NAME));
            if (isJobExists(jobKey)) throw new MicrofoxException("The job with same name and group already exists");
            JobDetail job = JobBuilder.newJob(DelegateJob.class)
                    .withIdentity(jobKey)
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
    public void job(Runnable task, String name, String group, String cronExpression, boolean disableConcurrentExecution) {
        register(task, name, group, cronExpression, disableConcurrentExecution);
    }

    @Override
    public void job(Runnable task, String name, String group, ZonedDateTime zonedDateTime) {
        register(task, name, group, Date.from(zonedDateTime.toInstant()));
    }

    @Override
    public void pauseJob(String name, String group) {
        try {
            scheduler.pauseJob(new JobKey(name, Optional.ofNullable(group).orElse(DEFAULT_JOB_GROUP_NAME)));
        } catch (SchedulerException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void resumeJob(String name, String group) {
        try {
            scheduler.resumeJob(new JobKey(name, Optional.ofNullable(group).orElse(DEFAULT_JOB_GROUP_NAME)));
        } catch (SchedulerException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void resumeAllJob() {
        try {
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pauseAllJob() {
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            throw new MicrofoxException(e);
        }
    }

    private boolean isJobExists(JobKey jobKey) {
        try {
            return scheduler.getCurrentlyExecutingJobs()
                    .stream()
                    .map(item -> item.getJobDetail().getKey())
                    .anyMatch(item -> item.getName().equals(jobKey.getName()) && item.getGroup().equals(jobKey.getGroup()));
        } catch (SchedulerException e) {
            throw new MicrofoxException(e);
        }
    }
}
