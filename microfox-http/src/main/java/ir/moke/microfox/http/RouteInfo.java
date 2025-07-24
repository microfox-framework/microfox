package ir.moke.microfox.http;

import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Route;

import java.util.Objects;

public record RouteInfo(Method method, String path, Route route) {

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