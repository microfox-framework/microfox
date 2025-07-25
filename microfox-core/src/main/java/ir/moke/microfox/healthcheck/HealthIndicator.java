package ir.moke.microfox.healthcheck;

public interface HealthIndicator {
    String name();
    HealthStatus check();
}
