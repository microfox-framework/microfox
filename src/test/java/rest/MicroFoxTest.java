package rest;

import ir.moke.microfox.job.JobSchedulerContainer;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

public class MicroFoxTest {
    public static void main(String[] str) throws Exception {
        ZonedDateTime localDateTime = ZonedDateTime.now().plusSeconds(10);
        JobSchedulerContainer.instance.register(Sample.class,Date.from(localDateTime.toInstant()));
    }
}
