package ir.moke.http;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.regex.Pattern;

@WebServlet("/*")
public class BaseServlet extends HttpServlet {
    private static Pattern compilePattern(String pattern) {
        String regex = pattern.replaceAll(":([^/]+)", "([^/]+)");
        return Pattern.compile("^" + regex + "$");
    }

    private static Optional<RouteInfo> findMatchingRouteInfo(String reqPath, Method method) {
        for (RouteInfo routeInfo : ResourceHolder.instance.list()) {
            String path = routeInfo.path();
            Pattern regex = compilePattern(path);
            if (regex.matcher(reqPath).matches() && method.equals(routeInfo.method())) {
                return Optional.of(routeInfo);
            }
        }
        return Optional.empty();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.GET)
                .ifPresentOrElse(item -> getHandle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.DELETE)
                .ifPresentOrElse(item -> getHandle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.PUT)
                .ifPresentOrElse(item -> getHandle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.HEAD)
                .ifPresentOrElse(item -> getHandle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.OPTIONS)
                .ifPresentOrElse(item -> getHandle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.PATCH)
                .ifPresentOrElse(item -> getHandle(req, resp, item), () -> notFound(resp));
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
        findMatchingRouteInfo(req.getRequestURI(), Method.TRACE)
                .ifPresentOrElse(item -> getHandle(req, resp, item), () -> notFound(resp));
    }

    private static void getHandle(HttpServletRequest req, HttpServletResponse resp, RouteInfo item) {
        item.route().handle(new Request(req), new Response(resp));
    }

    private static void notFound(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
