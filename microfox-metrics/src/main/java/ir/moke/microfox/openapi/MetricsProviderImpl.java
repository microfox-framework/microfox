package ir.moke.microfox.openapi;

import ir.moke.microfox.api.metrics.MetricsProvider;
import ir.moke.microfox.http.HttpContainer;
import ir.moke.microfox.openapi.filter.MetricFilter;
import ir.moke.microfox.openapi.servlet.MetricServlet;
import ir.moke.microfox.utils.TtyAsciiCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsProviderImpl implements MetricsProvider, TtyAsciiCodecs {
    private static final Logger logger = LoggerFactory.getLogger(MetricsProviderImpl.class);

    @Override
    public void registerMetrics() {
        /* Metrics */
        logger.info("{}{}{}", BACKGROUND_BLUE, "Metrics Activated", RESET);
        HttpContainer.addFilter(MetricFilter.class, "/*");
        HttpContainer.addServlet(MetricServlet.class, "/metrics");
    }
}
