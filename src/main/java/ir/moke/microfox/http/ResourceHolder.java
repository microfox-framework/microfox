package ir.moke.microfox.http;

import ir.moke.microfox.exception.MicrofoxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ir.moke.microfox.http.HttpUtils.concatContextPath;

public class ResourceHolder {
    private static final Logger logger = LoggerFactory.getLogger(ResourceHolder.class);
    private static final Set<RouteInfo> ROUTES = new HashSet<>();
    private static final List<FilterInfo> FILTERS = new ArrayList<>();
    public static final ResourceHolder instance = new ResourceHolder();
    private static final ExecutorService es = Executors.newSingleThreadExecutor();

    public void addRoute(Method method, String path, Route route) {
        if (!path.startsWith("/")) throw new MicrofoxException("route path should started with '/'");
        if (ResourceHolder.instance.listRoutes().isEmpty()) es.execute(MicroFoxHttpContainer::start);
        path = concatContextPath(path);
        logger.info("register route {} {}", method, path);
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
}