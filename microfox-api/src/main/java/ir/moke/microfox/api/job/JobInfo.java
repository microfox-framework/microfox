package ir.moke.microfox.api.job;

import java.util.Date;

public record JobInfo(String name,
                      String group,
                      int refireCount,
                      Date fireTime,
                      Date scheduledFireTime,
                      Date previousFireTime,
                      Date nextFireTime,
                      String fireInstanceId,
                      long jobRunTime) {
}
