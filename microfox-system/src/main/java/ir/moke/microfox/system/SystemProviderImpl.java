package ir.moke.microfox.system;

import ir.moke.microfox.api.system.SystemProvider;

import java.time.LocalTime;
import java.util.Timer;

public class SystemProviderImpl implements SystemProvider {
    private static final Timer timer = new Timer("microfox-system-task", true);

    static {
        onShutdown();
    }

    @Override
    public void activate() {
        LocalTime now = LocalTime.now();
        long delay = 10 - (now.getSecond() % 10);
        timer.scheduleAtFixedRate(new StatisticTask(), delay, 10);
    }

    private static void onShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(timer::cancel));
    }
}
