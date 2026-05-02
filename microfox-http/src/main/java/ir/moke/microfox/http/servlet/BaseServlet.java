package ir.moke.microfox.http.servlet;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.RouteInfo;
import ir.moke.microfox.http.HttpUtils;
import ir.moke.microfox.http.ResourceHolder;
import ir.moke.microfox.http.sse.SseInfo;
import ir.moke.microfox.http.sse.SseSubscriber;
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
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        HttpMethod httpMethod = HttpMethod.valueOf(req.getMethod().toUpperCase());
        String accept = req.getHeader("Accept");
        if (accept != null && accept.contains(ContentType.TEXT_EVENT_STREAM.getType())) {
            handleSse(req, resp);
        } else {
            RouteInfo routeInfo = findMatchingRouteInfo(req.getRequestURI(), httpMethod);
            if (routeInfo != null) {
                try {
                    handle(req, resp, routeInfo);
                } catch (Throwable e) {
                    HttpUtils.handleExceptionMapper(resp, e);
                }
            } else {
                notFound(resp);
            }
        }
    }

    private static void handle(HttpServletRequest req, HttpServletResponse resp, RouteInfo routeInfo) throws Throwable {
        Route route = routeInfo.route();
        route.handle(HttpUtils.getRequest(req), HttpUtils.getResponse(resp));
    }

    private static void handleSse(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType(ContentType.TEXT_EVENT_STREAM.getType());
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");

        final AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0);

        Optional<SseInfo> opt = ResourceHolder.getSsePublisher(req.getRequestURI());
        if (opt.isEmpty()) {
            notFound(resp);
            asyncContext.complete();
            return;
        }

        SseSubscriber subscriber = new SseSubscriber(HttpUtils.getResponse(resp), asyncContext, opt.get());
        opt.get().getPublisher().subscribe(subscriber);
    }

    private static void notFound(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
