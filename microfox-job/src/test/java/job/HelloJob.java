package job;

import ir.moke.microfox.api.job.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloJob implements Task {
    private static final Logger logger = LoggerFactory.getLogger(HelloJob.class);

    @Override
    public void run() {
        logger.info("Hello Job executed");
        sleep();
    }

    private static void sleep() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
