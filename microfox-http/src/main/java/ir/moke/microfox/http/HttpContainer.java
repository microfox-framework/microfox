package ir.moke.microfox.http;

import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.http.filter.BaseFilter;
import ir.moke.microfox.http.servlet.BaseServlet;
import ir.moke.microfox.http.servlet.MetricServlet;
import ir.moke.microfox.http.servlet.OpenApiServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

import static jakarta.servlet.DispatcherType.*;

public class HttpContainer {
    private static final Logger logger = LoggerFactory.getLogger(HttpContainer.class);
    private static final String contextPath = "/";
    private static final Integer HTTP_PORT = Integer.parseInt(HttpContainerConfig.MICROFOX_HTTP_PORT);
    private static final Integer HTTPS_PORT = Integer.parseInt(HttpContainerConfig.MICROFOX_HTTPS_PORT);
    private static final boolean FORCE_REDIRECT = Boolean.parseBoolean(HttpContainerConfig.MICROFOX_FORCE_REDIRECT_HTTPS);
    private static final Server server = new Server(HTTP_PORT);

    public static void start() {
        try {
            ServletContextHandler context = new ServletContextHandler();
            context.setContextPath(contextPath);

            /* Redoc & Swagger */
            context.addServlet(OpenApiServlet.class, "/docs");
            context.addServlet(OpenApiServlet.class, "/docs/*");

            /* Metrics */
            context.addServlet(MetricServlet.class, "/metrics");

            /* Rest Apis */
            context.addFilter(BaseFilter.class, "/*", EnumSet.of(FORWARD, ASYNC, REQUEST, INCLUDE, ERROR));
            context.addServlet(BaseServlet.class, "/*");

            server.setHandler(context);

            // Start the server
            server.start();
            server.join();
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }

    public static boolean isStarted() {
        return server.isStarted();
    }
}
