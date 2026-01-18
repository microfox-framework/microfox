package ir.moke.microfox.job;

import ir.moke.microfox.api.job.Task;
import org.quartz.JobKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TaskRegistry {
    private static final Map<JobKey, Task> TASKS = new ConcurrentHashMap<>();

    public static void register(JobKey key, Task task) {
        TASKS.put(key, task);
    }

    public static Task get(JobKey key) {
        return TASKS.get(key);
    }

    public static void remove(JobKey key) {
        TASKS.remove(key);
    }

    public static void removeAll() {
        TASKS.clear();
    }
}
