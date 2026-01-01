package ir.moke.microfox.api.job;

import java.time.ZonedDateTime;
import java.util.List;

public interface JobProvider {
    void job(Runnable task, String name, String group, String cronExpression, boolean disableConcurrentExecution);

    void job(Runnable task, String name, String group, ZonedDateTime zonedDateTime);

    void pauseJob(String name, String group);

    void resumeJob(String name, String group);

    void resumeAllJob();

    void pauseAllJob();

    void deleteJob(String name, String group);

    List<JobInfo> listJobs();

}
