package ir.moke.microfox.healthcheck;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HealthCheckManager {
    private static final List<HealthIndicator> indicators = new ArrayList<>();

    public static void register(HealthIndicator indicator) {
        indicators.add(indicator);
    }

    public static Map<String, HealthStatus> checkAll() {
        Map<String, HealthStatus> result = new LinkedHashMap<>();
        for (HealthIndicator i : indicators) {
            result.put(i.name(), i.check());
        }
        return result;
    }

    public static boolean isHealthy() {
        return indicators.stream().allMatch(i -> "UP".equals(i.check().getStatus()));
    }
}
