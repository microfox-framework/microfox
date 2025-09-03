package ir.moke.microfox.hc;

public interface HealthIndicator {
    String name();
    HealthStatus check();
}
