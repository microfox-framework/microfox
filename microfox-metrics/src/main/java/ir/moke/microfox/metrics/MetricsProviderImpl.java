package ir.moke.microfox.metrics;

import io.micrometer.core.instrument.Timer;
import ir.moke.microfox.api.http.FilterInfo;
import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.RouteInfo;
import ir.moke.microfox.api.metrics.MetricsProvider;
import ir.moke.microfox.http.ResourceHolder;
import ir.moke.utils.TtyAsciiCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MetricsProviderImpl implements MetricsProvider, TtyAsciiCodecs {
    private static final Logger logger = LoggerFactory.getLogger(MetricsProviderImpl.class);

    static {
        /* Metrics */
        logger.info("{}{}{}{}", "Metrics Activated", BACKGROUND_BLUE, "/metrics", RESET);
        ResourceHolder.addFilter(new FilterInfo("/*", -999, MetricHttp::handleFilter, "metrics", "microfox"));
        ResourceHolder.addRoute(new RouteInfo("/metrics", HttpMethod.GET, MetricHttp::handleRouter, "metrics", "microfox"));
    }

    @Override
    public void gauge(String name, double value) {
        Metrics.gauge(name, value);
    }

    @Override
    public void gauge(String name, Map<String, String> tags, double value) {
        Metrics.gauge(name, tags, value);
    }

    @Override
    public void timer(String name, Map<String, String> tags, Runnable runnable) {
        Timer.Sample sample = Timer.start(Metrics.registry());
        String exceptionName = "None";
        try {
            if (runnable != null) runnable.run();
        } catch (Exception e) {
            exceptionName = e.getClass().getSimpleName();
            throw e;
        } finally {
            tags.put("exception", exceptionName);
            sample.stop(Metrics.timer(name, tags));
        }
    }

    @Override
    public void counter(String name, Map<String, String> tags) {
        Metrics.counter(name, tags).increment();
    }
}
