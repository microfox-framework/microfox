package ir.moke.microfox.statistics;

import ir.moke.microfox.api.statistic.StatisticProvider;

import java.time.LocalTime;
import java.util.Timer;

public class StatisticProviderImpl implements StatisticProvider {
    private static final Timer timer = new Timer("microfox-statistic-task", true);

    static {
        onShutdown();
    }

    @Override
    public void activate() {
        LocalTime now = LocalTime.now();
        long delay = 5 - (now.getSecond() % 5);
        timer.scheduleAtFixedRate(new StatisticTask(), delay, 5);
    }

    private static void onShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(timer::cancel));
    }
}
