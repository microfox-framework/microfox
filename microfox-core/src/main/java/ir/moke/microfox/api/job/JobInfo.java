package ir.moke.microfox.api.job;

import java.util.Date;

public record JobInfo(String name,
                      String group,
                      int refireCount,
                      Date getFireTime,
                      Date getScheduledFireTime,
                      Date getPreviousFireTime,
                      Date getNextFireTime,
                      String getFireInstanceId,
                      long getJobRunTime) {
}
