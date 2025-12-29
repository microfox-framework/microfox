package ir.moke.microfox.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.util.List;

public class Metrics {
    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public static PrometheusMeterRegistry getRegistry() {
        return registry;
    }

    public static Counter counter(String name, List<Tag> tags) {
        return Counter.builder(name)
                .tags(tags)
                .register(registry);
    }

    public static Timer timer(String name,List<Tag> tags) {
        return Timer.builder(name)
                .tags(tags)
                .register(registry);
    }

    public static Gauge gauge(String name, Number value) {
        return Gauge.builder(name, value::doubleValue).register(registry);
    }
}
