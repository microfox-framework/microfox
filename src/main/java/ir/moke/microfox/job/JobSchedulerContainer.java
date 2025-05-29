package ir.moke.microfox.job;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.CronScheduleBuilder.cronSchedule;

public class JobSchedulerContainer {
    public static final JobSchedulerContainer instance = new JobSchedulerContainer();
    private final Scheduler scheduler;

    private JobSchedulerContainer() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Class<? extends Job> jobClass, String cronExpression) {
        try {
            JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobClass.getSimpleName()).build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(cronSchedule(cronExpression))
                    .build();

            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Class<? extends Job> jobClass, Date startAt) {
        try {
            JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobClass.getSimpleName()).build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .startAt(startAt)
                    .build();

            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
