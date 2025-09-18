package job;

public class HelloJob implements Runnable {
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
