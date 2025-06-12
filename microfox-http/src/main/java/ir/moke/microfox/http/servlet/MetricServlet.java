package ir.moke.microfox.http.servlet;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.http.Metrics;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class MetricServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String accept = Optional.ofNullable(req.getHeader("Accept")).orElse("text/plain");
        ContentType contentType = ContentType.fromValue(accept);
        if (contentType.equals(ContentType.TEXT_PLAIN)) {
            resp.setContentType(ContentType.TEXT_PLAIN.getType());
            String scrape = Metrics.getRegistry().scrape();
            resp.getWriter().write(scrape);
        } else if (contentType.equals(ContentType.APPLICATION_OPENMETRICS_TEXT)) {
            resp.setContentType(ContentType.APPLICATION_OPENMETRICS_TEXT.getType());
            String scrape = Metrics.getRegistry().scrape(ContentType.APPLICATION_OPENMETRICS_TEXT.getType());
            resp.getWriter().write(scrape);
        }
    }
}
