package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.util.List;
import java.util.Objects;

public record RouteInfo(Method method,
                        String path,
                        Route route,
                        SecurityStrategy strategy,
                        List<String> roles,
                        List<String> scopes) {
    public RouteInfo(Method method, String path, Route route) {
        this(method, path, route, null, List.of(), List.of());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RouteInfo routeInfo = (RouteInfo) o;
        return Objects.equals(path, routeInfo.path) && method == routeInfo.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }
}