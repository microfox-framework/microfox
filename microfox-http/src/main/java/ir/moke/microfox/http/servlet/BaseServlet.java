package ir.moke.microfox.http.servlet;

import io.micrometer.core.instrument.Timer;
import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static ir.moke.microfox.http.HttpUtils.findMatchingRouteInfo;

@WebServlet("/*")
public class BaseServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        String path = req.getRequestURI();
        String metricBase = method + "_" + path.replace("/", "_");

        Metrics.counter(metricBase + "_count").increment();
        Timer.Sample sample = Timer.start(Metrics.getRegistry());
        try {
            super.service(req, resp);
        } finally {
            sample.stop(Metrics.timer(metricBase + "_latency"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String accept = req.getHeader("Accept");
        if (accept.equalsIgnoreCase(ContentType.TEXT_EVENT_STREAM.getType())) {
            resp.setContentType(ContentType.TEXT_EVENT_STREAM.getType());
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setHeader("Connection", "keep-alive");
            findMatchingRouteInfo(req.getRequestURI(), Method.GET)
                    .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
        } else {
            findMatchingRouteInfo(req.getRequestURI(), Method.GET)
                    .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.POST)
                .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.DELETE)
                .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.PUT)
                .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.HEAD)
                .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.OPTIONS)
                .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.PATCH)
                .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.TRACE)
                .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
    }

    private static void handle(HttpServletRequest req, HttpServletResponse resp, RouteInfo item) {
        try {
            item.route().handle(new RequestImpl(req), new ResponseImpl(resp));
        } catch (Exception e) {
            if (e.getClass().isAssignableFrom(ValidationException.class)) {
                logger.debug("Validation exception {}", e.getLocalizedMessage());
                handleExceptionResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e);
            } else {
                logger.error("Microfox Unknown Error", e);
                handleExceptionResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        }
    }

    private static void handleExceptionResponse(HttpServletResponse resp, int statusCode, Exception e) {
        try {
            resp.setStatus(statusCode);
            String localizedMessage = e.getCause().getLocalizedMessage();
            String message = e.getCause().getMessage();
            resp.getWriter().write((localizedMessage != null && !localizedMessage.isEmpty()) ? localizedMessage : message);
        } catch (IOException io) {
            logger.error("Microfox IO Error", io);
        }
    }

    private static void notFound(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
