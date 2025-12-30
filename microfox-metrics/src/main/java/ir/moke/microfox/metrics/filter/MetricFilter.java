package ir.moke.microfox.metrics.filter;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import ir.moke.microfox.metrics.Metrics;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class MetricFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String method = req.getMethod();
        String path = req.getRequestURI();
        String metricBase = method + "_" + path.replace("/", "_");

        Timer.Sample sample = Timer.start(Metrics.getRegistry());
        String exceptionName = "None";
        try {
            filterChain.doFilter(req, resp);
        } catch (Exception e) {
            exceptionName = e.getClass().getSimpleName();
        } finally {
            int status = resp.getStatus();
            String outcome = getOutcomeFromStatus(status);
            List<Tag> tags = List.of(
                    Tag.of("method", method),
                    Tag.of("uri", normalizeUri(path)),
                    Tag.of("status", String.valueOf(status)),
                    Tag.of("exception", exceptionName),
                    Tag.of("outcome", outcome)
            );

            sample.stop(Metrics.timer(metricBase + "_latency", tags));
            Metrics.counter(metricBase + "_count", tags).increment();
        }
    }

    private String getOutcomeFromStatus(int status) {
        if (status >= 100 && status < 400) return "SUCCESS";
        if (status >= 400 && status < 500) return "CLIENT_ERROR";
        if (status >= 500) return "SERVER_ERROR";
        return "UNKNOWN";
    }

    static String normalizeUri(String uri) {
        return uri.replaceAll("/\\d+", "/{id}");
    }
}
