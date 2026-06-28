package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.security.SecurityStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class RouteInfo {

    private final HttpMethod httpMethod;
    private final String path;
    private final Route route;
    private SecurityStrategy strategy;
    private List<String> roles = new ArrayList<>();
    private List<String> scopes = new ArrayList<>();
    private final Pattern pattern;
    private String name;
    private String category;

    public RouteInfo(HttpMethod httpMethod, String path, Route route, SecurityStrategy strategy, List<String> roles, List<String> scopes) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.route = route;
        this.strategy = strategy;
        this.roles = roles;
        this.scopes = scopes;
        pattern = HttpUtils.compilePattern(path);
    }

    public RouteInfo(HttpMethod httpMethod, String path, Route route) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.route = route;
        pattern = HttpUtils.compilePattern(path);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Route getRoute() {
        return route;
    }

    public SecurityStrategy getStrategy() {
        return strategy;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RouteInfo routeInfo = (RouteInfo) o;
        return Objects.equals(path, routeInfo.path) && httpMethod == routeInfo.httpMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path);
    }
}