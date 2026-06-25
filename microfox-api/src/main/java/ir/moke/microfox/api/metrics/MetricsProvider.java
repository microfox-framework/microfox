package ir.moke.microfox.api.metrics;

import java.util.Map;
import java.util.function.Supplier;

public interface MetricsProvider {
    void gauge(String name, double value);

    void gauge(String name, Map<String, String> tags, double value);

    void timer(String name, Map<String, String> tags, Runnable runnable);

    void counter(String name, Map<String, String> tags);
}
