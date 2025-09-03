package ir.moke.microfox.http.servlet;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.ResponseObject;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.api.http.sse.SseInfo;
import ir.moke.microfox.api.http.sse.SseSubscriber;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.ExceptionMapperHolder;
import ir.moke.microfox.http.RequestImpl;
import ir.moke.microfox.http.ResourceHolder;
import ir.moke.microfox.http.ResponseImpl;
import ir.moke.microfox.http.RouteInfo;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
            routeInfo.route().handle(new RequestImpl(req), new ResponseImpl(resp));
        } catch (Exception e) {
            ExceptionMapper<Throwable> mapper = ExceptionMapperHolder.get(e);
            if (mapper != null) {
                ResponseObject ro = mapper.toResponse(e);
                Optional.ofNullable(ro.getStatusCode()).ifPresent(item -> resp.setStatus(item.getCode()));
                Optional.ofNullable(ro.getContentType()).ifPresent(item -> resp.setContentType(item.getType()));
                Optional.ofNullable(ro.getHeaders()).ifPresent(item -> fillExtraHeaders(resp, item));
                Optional.ofNullable(ro.getLocale()).ifPresent(resp::setLocale);
                Optional.ofNullable(ro.getCharacterEncoding()).ifPresent(resp::setCharacterEncoding);
                Optional.ofNullable(ro.getCookies()).ifPresent(item -> item.forEach(resp::addCookie));
                Optional.ofNullable(ro.getBody()).ifPresent(item -> sendResponse(resp, item));
            } else {
                logger.error("Microfox Unknown Error", e);
                resp.setStatus(StatusCode.INTERNAL_SERVER_ERROR.getCode());
                sendResponse(resp, e.getMessage().getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private static void fillExtraHeaders(HttpServletResponse resp, Map<String, Object> headers) {
        headers.forEach((k, v) -> {
            if (v instanceof Integer i) resp.addIntHeader(k, i);
            if (v instanceof Long l) resp.addDateHeader(k, l);
            if (v instanceof String s) resp.addHeader(k, s);
        });
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
        SseSubscriber subscriber = new SseSubscriber(new ResponseImpl(resp));
        info.getPublisher().subscribe(subscriber);
    }

    private static void sendResponse(HttpServletResponse resp, byte[] bytes) {
        try (ServletOutputStream os = resp.getOutputStream()) {
            os.write(bytes);
            os.flush();
        } catch (IOException io) {
            logger.error("Microfox IO Error", io);
        }
    }

    private static void notFound(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
