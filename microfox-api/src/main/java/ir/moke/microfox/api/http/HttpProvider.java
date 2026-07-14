package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.sse.SseObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface HttpProvider {
    void filter(String path, int order, Filter filter);

    void filter(String path, int order, String description, String category, Filter filter);

    void filter(FilterInfo filterInfo);

    List<FilterInfo> filterList();

    void filterSort(String... sortedHash);

    void route(String path, HttpMethod httpMethod, Route route);

    void route(String path, HttpMethod httpMethod, String name, String category, Route route);

    void route(String path, HttpMethod httpMethod, Route route, List<String> roles, List<String> scopes);

    void route(RouteInfo routeInfo);

    Set<RouteInfo> routeList();

    void websocket(Class<?> endpointClass);

    void sseRegister(String identity, String path);

    void ssePublisher(String identity, SseObject sseObject);

    void security(SecurityInfo securityInfo);

    void removeRoute(String path, HttpMethod method);

    void removeRoute(String category);

    void removeFilter(String category);

    void cors(Map<CORSHeader, String> valueMap);

    void corsAccessAll();
}
