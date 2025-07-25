package ir.moke.microfox.healthcheck;

public class HealthStatus {
    private final String status;
    private final String message;

    private HealthStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static HealthStatus up() {
        return new HealthStatus("UP", "OK");
    }

    public static HealthStatus down(String msg) {
        return new HealthStatus("DOWN", msg);
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
