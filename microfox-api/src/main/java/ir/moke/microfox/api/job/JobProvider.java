package ir.moke.microfox.api.job;

import java.time.ZonedDateTime;
import java.util.Set;

public interface JobProvider {
    void job(Task task, String name, String group, String cronExpression, JobOption option);

    void job(Task task, String name, String group, ZonedDateTime zonedDateTime, JobOption option);

    void job(Task task, String name, String group, Set<String> cronExpressions, Set<ZonedDateTime> onceExecutions, JobOption option);

    void jobTrigger(String name, String group);

    void pauseJob(String name, String group);

    void resumeJob(String name, String group);

    void resumeAllJob();

    void pauseAllJob();

    void deleteJob(String name, String group);
}
