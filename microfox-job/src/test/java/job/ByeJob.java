package job;

import ir.moke.microfox.api.job.Task;

public class ByeJob implements Task {
    @Override
    public void run() {
        System.out.println("Bye Job executed");
    }
}
