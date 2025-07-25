package ir.moke.microfox.openapi;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class Metrics {
    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public static PrometheusMeterRegistry getRegistry() {
        return registry;
    }

    public static Counter counter(String name) {
        return registry.counter(name);
    }

    public static Timer timer(String name) {
        return registry.timer(name);
    }

    public static Gauge gauge(String name, Number value) {
        return Gauge.builder(name, value::doubleValue).register(registry);
    }
}
