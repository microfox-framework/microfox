package ir.moke.microfox.api.job;

import java.time.ZonedDateTime;

public interface JobProvider {
    void job(Runnable task, String cronExpression) ;

    void job(Runnable task, ZonedDateTime zonedDateTime);

}
