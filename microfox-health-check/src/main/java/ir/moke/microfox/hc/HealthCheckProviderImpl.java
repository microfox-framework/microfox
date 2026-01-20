package ir.moke.microfox.hc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.hc.HealthCheckProvider;
import ir.moke.microfox.exception.MicroFoxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class HealthCheckProviderImpl implements HealthCheckProvider {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckProviderImpl.class);
    private static final String host = MicroFoxEnvironment.getEnv("microfox.health.check.host");
    private static final String port = MicroFoxEnvironment.getEnv("microfox.health.check.port");
    private static final String path = MicroFoxEnvironment.getEnv("microfox.health.api.path");
    private static final Boolean active = Boolean.parseBoolean(MicroFoxEnvironment.getEnv("microfox.health.check.active"));
    private static final HttpServer server;

    static {
        try {
            server = HttpServer.create(new InetSocketAddress(host, Integer.parseInt(port)), 0);
        } catch (IOException e) {
            throw new MicroFoxException(e);
        }
    }

    private static void healthCheckController(HttpExchange exchange) throws IOException {
        Map<String, HealthStatus> results = HealthCheckManager.checkAll();

        boolean allUp = HealthCheckManager.isHealthy();
        StringBuilder sb = new StringBuilder("{\n  \"status\": \"")
                .append(allUp ? "UP" : "DOWN").append("\",\n  \"details\": {\n");

        for (Map.Entry<String, HealthStatus> entry : results.entrySet()) {
            sb.append("    \"").append(entry.getKey()).append("\": \"")
                    .append(entry.getValue().getStatus()).append("\",\n");
        }

        if (!results.isEmpty()) sb.setLength(sb.length() - 2); // remove last comma
        sb.append("\n  }\n}");

        byte[] body = sb.toString().getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(allUp ? 200 : 503, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    @Override
    public void activate() {
        if (!active) return;
        server.createContext(MicroFoxEnvironment.getEnv("microfox.health.api.path"), HealthCheckProviderImpl::healthCheckController);
        server.start();
        logger.info("Health check HTTP server started at : http://{}:{}{}", host, port, path);
    }
}
