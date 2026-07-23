package ir.moke.microfox.metrics;

import io.micrometer.core.instrument.Timer;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MetricHttp {
    private static final Logger logger = LoggerFactory.getLogger(MetricHttp.class);

    public static void handle(Request request, Response response) {
        String accept = Optional.ofNullable(request.header("Accept")).orElse("text/plain");
        ContentType contentType = Optional.ofNullable(ContentType.fromValue(accept)).orElse(ContentType.TEXT_PLAIN);
        if (contentType.equals(ContentType.TEXT_PLAIN)) {
            response.contentType(ContentType.TEXT_PLAIN);
            String scrape = Metrics.registry().scrape();
            response.body(scrape);
        } else if (contentType.equals(ContentType.APPLICATION_OPENMETRICS_TEXT)) {
            response.contentType(ContentType.APPLICATION_OPENMETRICS_TEXT);
            String scrape = Metrics.registry().scrape(ContentType.APPLICATION_OPENMETRICS_TEXT.getType());
            response.body(scrape);
        }
    }

    public static void handleFilter(Request request, Response response, Chain chain) {
        HttpMethod method = request.getMethod();
        String path = request.pathInfo();
        String metricBase = method + "_" + path.replace("/", "_");
        logger.trace("Filter metric {}", metricBase);

        String exceptionName = "None";
        Timer.Sample sample = Timer.start(Metrics.registry());
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            exceptionName = e.getClass().getSimpleName();
            throw e;
        } finally {
            int status = response.status();
            String outcome = getOutcomeFromStatus(status);

            Map<String, String> tags = new HashMap<>();
            tags.put("method", method.name());
            tags.put("uri", normalizeUri(path));
            tags.put("status", String.valueOf(status));
            tags.put("outcome", outcome);
            tags.put("exception", exceptionName);

            sample.stop(Metrics.timer(metricBase, tags));
            MicroFox.metricCounter(metricBase + "_count", tags);
        }
    }

    private static String getOutcomeFromStatus(int status) {
        if (status >= 100 && status < 400) return "SUCCESS";
        if (status >= 400 && status < 500) return "CLIENT_ERROR";
        if (status >= 500) return "SERVER_ERROR";
        return "UNKNOWN";
    }

    private static String normalizeUri(String uri) {
        return uri.replaceAll("/\\d+", "/{id}");
    }
}
