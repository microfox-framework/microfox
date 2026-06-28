package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.api.http.sse.SseObject;

import java.util.List;
import java.util.Set;

public interface HttpProvider {
    void filter(String path, int order, Filter filter);

    void filter(String path, int order, String name, String category, Filter filter);

    void filter(FilterInfo filterInfo);

    List<FilterInfo> filterList();

    void filterSort(String... sortedHash);

    void route(String path, HttpMethod httpMethod, Route route);
    void route(String path, HttpMethod httpMethod, String name, String category, Route route);

    void route(String path, HttpMethod httpMethod, Route route, SecurityStrategy strategy, List<String> roles, List<String> scopes);

    void route(RouteInfo routeInfo);

    Set<RouteInfo> routeList();

    void remove(String path, HttpMethod method);

    void websocket(Class<?> endpointClass);

    void sseRegister(String identity, String path);

    void ssePublisher(String identity, SseObject sseObject);

}
