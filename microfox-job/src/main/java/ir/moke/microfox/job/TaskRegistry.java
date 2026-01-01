package ir.moke.microfox.job;

import org.quartz.JobKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRegistry {
    private static final Map<JobKey, Runnable> TASKS = new ConcurrentHashMap<>();

    public static void register(JobKey key, Runnable task) {
        TASKS.put(key, task);
    }

    public static Runnable get(JobKey key) {
        return TASKS.get(key);
    }

    public static void remove(JobKey key) {
        TASKS.remove(key);
    }

    public static void removeAll() {
        TASKS.clear();
    }
}
