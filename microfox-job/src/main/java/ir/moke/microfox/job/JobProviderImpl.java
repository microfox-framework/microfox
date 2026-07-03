package ir.moke.microfox.job;

import ir.moke.microfox.api.job.JobInfo;
import ir.moke.microfox.api.job.JobProvider;
import ir.moke.microfox.api.job.Task;
import ir.moke.microfox.exception.MicroFoxException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private void register(Task task, String name, String group, Date startAt) {
        try {
            JobKey jobKey = new JobKey(name, DEFAULT_JOB_NAME.apply(group));
            if (isJobExists(jobKey)) throw new MicroFoxException("The job with same name and group already exists");
            TaskRegistry.register(jobKey, task);
            JobDetail job = JobBuilder.newJob(DelegateJob.class)
                    .withIdentity(jobKey)
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
    public void job(Task task, String name, String group, String cronExpression, boolean concurrentExecution) {
        register(task, name, group, cronExpression, concurrentExecution);
    }

    @Override
    public void job(Task task, String name, String group, Date date) {
        register(task, name, group, date);
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

    public List<JobInfo> listJobs() {
        List<JobInfo> jobInfos = new ArrayList<>();
        try {
            for (JobExecutionContext currentlyExecutingJob : scheduler.getCurrentlyExecutingJobs()) {
                String name = currentlyExecutingJob.getJobDetail().getKey().getName();
                String group = currentlyExecutingJob.getJobDetail().getKey().getGroup();
                int refireCount = currentlyExecutingJob.getRefireCount();
                Date fireTime = currentlyExecutingJob.getFireTime();
                Date scheduledFireTime = currentlyExecutingJob.getScheduledFireTime();
                Date previousFireTime = currentlyExecutingJob.getPreviousFireTime();
                Date nextFireTime = currentlyExecutingJob.getNextFireTime();
                String fireInstanceId = currentlyExecutingJob.getFireInstanceId();
                long jobRunTime = currentlyExecutingJob.getJobRunTime();

                JobInfo jobInfo = new JobInfo(
                        name,
                        group,
                        refireCount,
                        fireTime,
                        scheduledFireTime,
                        previousFireTime,
                        nextFireTime,
                        fireInstanceId,
                        jobRunTime);

                jobInfos.add(jobInfo);
            }
        } catch (SchedulerException e) {
            throw new MicroFoxException(e);
        }
        return jobInfos;
    }

    @Override
    public JobInfo job(String name, String group) {
        return listJobs().stream()
                .filter(item -> item.name().equals(name))
                .filter(item -> item.group().equals(group))
                .findAny()
                .orElse(null);
    }

    private boolean isJobExists(JobKey jobKey) {
        try {
            return scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            throw new MicroFoxException(e);
        }
    }
}
