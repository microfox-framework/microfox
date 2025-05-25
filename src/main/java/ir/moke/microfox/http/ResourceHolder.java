package ir.moke.microfox.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceHolder {
    private static final Logger logger = LoggerFactory.getLogger(ResourceHolder.class);
    private static final Set<RouteInfo> ROUTES = new HashSet<>();
    private static final List<FilterInfo> FILTERS = new ArrayList<>();
    public static final ResourceHolder instance = new ResourceHolder();

    private ResourceHolder() {
        new Thread(HttpContainer::start).start();
    }


    public void addRoute(Method method, String path, Route route) {
        logger.info("register route {} {}", method, path);
        ROUTES.add(new RouteInfo(method, path, route));
    }

    public Set<RouteInfo> listRoutes() {
        return ROUTES;
    }

    public void addFilter(String path, Filter... filters) {
        logger.info("register filter {}", path);
        for (Filter filter : filters) {
            FILTERS.add(new FilterInfo(path, filter));
        }
    }

    public List<FilterInfo> listFilters() {
        return FILTERS;
    }
}