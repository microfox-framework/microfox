package ir.moke.microfox.http;

import ir.moke.microfox.api.http.Filter;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.RouteInfo;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.http.sse.SseInfo;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;

import static ir.moke.microfox.http.HttpUtils.concatContextPath;
import static ir.moke.utils.TtyAsciiCodecs.*;

public class ResourceHolder {
    private static final Logger logger = LoggerFactory.getLogger(ResourceHolder.class);
    private static final Set<RouteInfo> ROUTES = ConcurrentHashMap.newKeySet();
    private static final List<FilterInfo> FILTERS = new ArrayList<>();
    private static final Set<SseInfo> SSE_LIST = ConcurrentHashMap.newKeySet();
    private static final Set<Class<?>> WEBSOCKET_LIST = ConcurrentHashMap.newKeySet();
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    public static final ExecutorService SSE_EXECUTOR = Executors.newCachedThreadPool();

    public static void addRoute(Method method, String path, Route route) {
        addRoute(method, path, route, null, List.of(), List.of());
    }

    public static void addRoute(Method method, String path, Route route, SecurityStrategy strategy, List<String> roles, List<String> scopes) {
        if (!path.startsWith("/")) throw new MicroFoxException("route path should started with '/'");
        if (!HttpContainer.isStarted()) EXECUTOR.execute(HttpContainer::start);
        path = concatContextPath(path);
        logger.info("register route {}{} {}{}{}", BLUE, method, GREEN, path, RESET);
        ROUTES.add(new RouteInfo(method, path, route, strategy, roles, scopes));
    }

    public static void removeRoute(Method method, String path) {
        ROUTES.removeIf(item -> item.path().equals(path) && item.method().equals(method));
    }

    public static Set<RouteInfo> listRoutes() {
        return ROUTES;
    }

    public static void addFilter(String path, Filter... filters) {
        if (!path.startsWith("/")) throw new MicroFoxException("filter path should started with '/'");
        path = concatContextPath(path);
        logger.info("register filter {}{}{}", GREEN, path, RESET);
        for (Filter filter : filters) {
            FILTERS.add(new FilterInfo(path, filter));
        }
    }

    public static void addWebsocket(Class<?> wsClass) {
        if (!wsClass.isAnnotationPresent(ServerEndpoint.class))
            throw new MicroFoxException("Websocket endpoint should annotated by ServerEndpoint.class");
        logger.info("register websocket {}{}{}", GREEN, wsClass.getName(), RESET);
        WEBSOCKET_LIST.add(wsClass);
    }

    public static Set<Class<?>> listWebsockets() {
        return WEBSOCKET_LIST;
    }

    public static List<FilterInfo> listFilters() {
        return FILTERS;
    }

    public static void registerSse(String identity, String path) {
        logger.info("register sse {}{}{}", GREEN, path, RESET);
        if (!HttpContainer.isStarted()) EXECUTOR.execute(HttpContainer::start);
        SSE_LIST.add(new SseInfo(identity, concatContextPath(path)));
    }

    public static Optional<SseInfo> getSsePublisher(String path) {
        return SSE_LIST.stream().filter(item -> item.getPath().equals(path)).findFirst();
    }

    public static SubmissionPublisher<SseObject> getSseByIdentity(String identity) {
        return SSE_LIST.stream()
                .filter(item -> item.getIdentity().equals(identity))
                .map(SseInfo::getPublisher)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public static void removeSse(SseInfo sseInfo) {
        SSE_LIST.remove(sseInfo);
    }

    public boolean isSseRegistered(String path) {
        return SSE_LIST.stream().anyMatch(item -> item.getPath().equals(path));
    }

    public static void closeAllSse() {
        SSE_LIST.forEach(sse -> sse.getPublisher().close());
        SSE_LIST.clear();
    }
}