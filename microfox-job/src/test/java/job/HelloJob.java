package job;

import ir.moke.microfox.api.job.Task;

public class HelloJob implements Task {
    @Override
    public void run() {
        System.out.println("Hello Job executed");
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
