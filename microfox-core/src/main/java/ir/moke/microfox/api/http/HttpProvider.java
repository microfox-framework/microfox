package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.api.http.sse.SseObject;

import java.util.List;

public interface HttpProvider {
    void filter(String path, Filter... filters);

    void http(String path, Method method, Route route);

    void http(String path, Method method, Route route, SecurityStrategy strategy, List<String> roles, List<String> scopes);

    void http(RouteInfo routeInfo);

    void websocket(Class<?> endpointClass);

    void sseRegister(String identity, String path);

    void ssePublisher(String identity, SseObject sseObject);
}
