package ir.moke.http;

import java.util.HashSet;
import java.util.Set;

public class ResourceHolder {
    private static final Set<RouteInfo> ROUTES = new HashSet<>();
    public static final ResourceHolder instance = new ResourceHolder();

    private ResourceHolder() {
        new Thread(HttpContainer::start).start();
    }

    public void add(Method method, String path, Route route) {
        ROUTES.add(new RouteInfo(method, path, route));
    }

    public Set<RouteInfo> list() {
        return ROUTES;
    }
}