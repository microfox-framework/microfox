package ir.moke.microfox.api.job;

import java.time.ZonedDateTime;

public interface JobProvider {
    void job(Runnable task, String name, String group, String cronExpression, boolean disableConcurrentExecution);

    void job(Runnable task, String name, String group, ZonedDateTime zonedDateTime);

    void pauseJob(String name, String group);

    void resumeJob(String name, String group);

    void resumeAllJob();

    void pauseAllJob();
}
