package ir.moke.microfox.metrics.filter;

import io.micrometer.core.instrument.Timer;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.metrics.Metrics;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MetricFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String method = req.getMethod();
        String path = req.getRequestURI();
        String metricBase = method + "_" + path.replace("/", "_");

        String exceptionName = "None";
        Timer.Sample sample = Timer.start(Metrics.registry());
        try {
            filterChain.doFilter(req, resp);
        } catch (Exception e) {
            exceptionName = e.getClass().getSimpleName();
            throw e;
        } finally {
            int status = resp.getStatus();
            String outcome = getOutcomeFromStatus(status);

            Map<String, String> tags = new HashMap<>();
            tags.put("method", method);
            tags.put("uri", normalizeUri(path));
            tags.put("status", String.valueOf(status));
            tags.put("outcome", outcome);
            tags.put("exception", exceptionName);

            sample.stop(Metrics.timer(metricBase, tags));
            MicroFox.metricCounter(metricBase + "_count", tags);
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
