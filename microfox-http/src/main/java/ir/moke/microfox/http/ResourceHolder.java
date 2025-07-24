package ir.moke.microfox.http;

import ir.moke.microfox.api.http.Filter;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.sse.SseInfo;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicrofoxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;

import static ir.moke.microfox.http.HttpUtils.concatContextPath;
import static ir.moke.microfox.utils.TtyAsciiCodecs.*;

public class ResourceHolder {
    private static final Logger logger = LoggerFactory.getLogger(ResourceHolder.class);
    private static final Set<RouteInfo> ROUTES = new HashSet<>();
    private static final List<FilterInfo> FILTERS = new ArrayList<>();
    private static final Set<SseInfo> SSE_LIST = new HashSet<>();
    public static final ResourceHolder instance = new ResourceHolder();
    private static final ExecutorService es = Executors.newSingleThreadExecutor();

    public void addRoute(Method method, String path, Route route) {
        if (!path.startsWith("/")) throw new MicrofoxException("route path should started with '/'");
        if (!HttpContainer.isStarted()) es.execute(HttpContainer::start);
        path = concatContextPath(path);
        logger.info("register route {}{} {}{}{}", BLUE, method, GREEN, path, RESET);
        ROUTES.add(new RouteInfo(method, path, route));
    }

    public Set<RouteInfo> listRoutes() {
        return ROUTES;
    }

    public void addFilter(String path, Filter... filters) {
        if (!path.startsWith("/")) throw new MicrofoxException("filter path should started with '/'");
        path = concatContextPath(path);
        logger.info("register filter {}", path);
        for (Filter filter : filters) {
            FILTERS.add(new FilterInfo(path, filter));
        }
    }

    public List<FilterInfo> listFilters() {
        return FILTERS;
    }

    public void registerSse(String identity, String path) {
        logger.info("register sse {}{}{}", GREEN, path, RESET);
        if (!HttpContainer.isStarted()) es.execute(HttpContainer::start);
        SSE_LIST.add(new SseInfo(identity, concatContextPath(path)));
    }
    public Optional<SseInfo> getSsePublisher(String path) {
        return SSE_LIST.stream().filter(item -> item.getPath().equals(path)).findFirst();
    }

    public SubmissionPublisher<SseObject> getSseByIdentity(String identity) {
        return SSE_LIST.stream()
                .filter(item -> item.getIdentity().equals(identity))
                .map(SseInfo::getPublisher)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public boolean isSseRegistered(String path) {
        return SSE_LIST.stream().anyMatch(item -> item.getPath().equals(path));
    }
}