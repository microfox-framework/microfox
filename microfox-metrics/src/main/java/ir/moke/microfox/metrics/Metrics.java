package ir.moke.microfox.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class Metrics {
    private static final PrometheusMeterRegistry REGISTRY = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    private static final ConcurrentMap<String, AtomicReference<Double>> GAUGES = new ConcurrentHashMap<>();

    private Metrics() {
    }

    public static PrometheusMeterRegistry registry() {
        return REGISTRY;
    }

    public static Counter counter(String name, Map<String, String> tags) {
        return Counter.builder(name)
                .tags(toTags(tags))
                .register(REGISTRY);
    }

    public static Timer timer(String name, Map<String, String> tags) {
        return Timer.builder(name)
                .tags(toTags(tags))
                .register(REGISTRY);
    }

    public static void gauge(String name, double value) {
        gauge(name, Collections.emptyMap(), value);
    }

    public static void gauge(String name, Map<String, String> tags, double value) {
        String key = meterKey(name, tags);
        AtomicReference<Double> ref = GAUGES.computeIfAbsent(key, k -> {
            AtomicReference<Double> holder = new AtomicReference<>(0.0);
            Gauge.builder(name, holder, AtomicReference::get).tags(toTags(tags)).register(REGISTRY);
            return holder;
        });

        ref.set(value);
    }

    private static List<Tag> toTags(Map<String, String> tags) {
        return tags.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> Tag.of(e.getKey(), e.getValue()))
                .toList();
    }

    private static String meterKey(String name, Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) return name;
        String tagPart = tags.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(","));
        return name + "|" + tagPart;
    }
}