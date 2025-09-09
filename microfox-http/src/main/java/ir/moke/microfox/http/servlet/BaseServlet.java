package ir.moke.microfox.http.servlet;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.sse.SseInfo;
import ir.moke.microfox.api.http.sse.SseSubscriber;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.http.HttpUtils;
import ir.moke.microfox.http.ResourceHolder;
import ir.moke.microfox.http.RouteInfo;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static ir.moke.microfox.http.HttpUtils.findMatchingRouteInfo;

public class BaseServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String accept = req.getHeader("Accept");
        if (accept != null && accept.equalsIgnoreCase(ContentType.TEXT_EVENT_STREAM.getType())) {
            handleSse(req, resp);
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
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.TRACE)
                .ifPresentOrElse(item -> handle(req, resp, item), () -> notFound(resp));
    }

    private static void handle(HttpServletRequest req, HttpServletResponse resp, RouteInfo routeInfo) {
        try {
            Route route = routeInfo.route();
            route.handle(HttpUtils.getRequest(req), HttpUtils.getResponse(resp));
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }


    private static void handleSse(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType(ContentType.TEXT_EVENT_STREAM.getType());
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");

        final AsyncContext async = req.startAsync();
        async.setTimeout(0);

        Optional<SseInfo> opt = ResourceHolder.getSsePublisher(req.getRequestURI());
        if (opt.isEmpty()) {
            notFound(resp);
            return;
        }

        SseInfo info = opt.get();
        SseSubscriber subscriber = new SseSubscriber(HttpUtils.getResponse(resp));
        info.getPublisher().subscribe(subscriber);
    }

    private static void notFound(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
