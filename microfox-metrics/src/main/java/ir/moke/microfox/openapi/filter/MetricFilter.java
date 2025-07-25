package ir.moke.microfox.openapi.filter;

import io.micrometer.core.instrument.Timer;
import ir.moke.microfox.openapi.Metrics;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MetricFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String method = req.getMethod();
        String path = req.getRequestURI();
        String metricBase = method + "_" + path.replace("/", "_");

        Metrics.counter(metricBase + "_count").increment();
        Timer.Sample sample = Timer.start(Metrics.getRegistry());

        try {
            filterChain.doFilter(req, resp);
        } finally {
            sample.stop(Metrics.timer(metricBase + "_latency"));
        }
    }
}
