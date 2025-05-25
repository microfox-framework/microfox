package ir.moke.http;

import java.util.Objects;

public class RouteInfo {
    private Method method;
    private String path;
    private Route route;

    public RouteInfo() {
    }

    public RouteInfo(Method method, String path, Route route) {
        this.method = method;
        this.path = path;
        this.route = route;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
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