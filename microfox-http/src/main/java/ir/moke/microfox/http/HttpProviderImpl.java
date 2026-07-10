package ir.moke.microfox.http;

import ir.moke.microfox.api.http.*;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicroFoxException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.SubmissionPublisher;

public class HttpProviderImpl implements HttpProvider {

    @Override
    public void filter(String path, int order, Filter filter) {
        ResourceHolder.addFilter(new FilterInfo(path, order, filter));
    }

    @Override
    public void filter(String path, int order, String name, String category, Filter filter) {
        ResourceHolder.addFilter(new FilterInfo(path, order, filter, name, category));
    }

    @Override
    public void filter(FilterInfo filterInfo) {
        ResourceHolder.addFilter(filterInfo);
    }

    @Override
    public List<FilterInfo> filterList() {
        return ResourceHolder.listFilters();
    }

    @Override
    public void filterSort(String... sortedHash) {
        for (int i = 0; i < sortedHash.length; i++) {
            String hash = sortedHash[i];
            FilterInfo filterInfo = ResourceHolder.getFilterByHash(hash);
            filterInfo.setOrder(i);
        }
    }

    @Override
    public void route(String path, HttpMethod httpMethod, Route route) {
        ResourceHolder.addRoute(new RouteInfo(path, httpMethod, route));
    }

    @Override
    public void route(String path, HttpMethod httpMethod, String name, String category, Route route) {
        ResourceHolder.addRoute(new RouteInfo(path, httpMethod, route, name, category));
    }

    @Override
    public void route(String path, HttpMethod httpMethod, Route route, List<String> roles, List<String> scopes) {
        ResourceHolder.addRoute(new RouteInfo(path, httpMethod, route, roles, scopes));
    }

    @Override
    public void route(RouteInfo routeInfo) {
        ResourceHolder.addRoute(routeInfo);
    }

    @Override
    public Set<RouteInfo> routeList() {
        return ResourceHolder.listRoutes();
    }

    @Override
    public void remove(String path, HttpMethod method) {
        ResourceHolder.removeRoute(path, method);
    }

    @Override
    public void websocket(Class<?> endpointClass) {
        ResourceHolder.addWebsocket(endpointClass);
    }

    @Override
    public void sseRegister(String identity, String path) {
        ResourceHolder.registerSse(identity, path);
    }

    @Override
    public void ssePublisher(String identity, SseObject sseObject) {
        SubmissionPublisher<SseObject> submissionPublisher = ResourceHolder.getSseByIdentity(identity);
        if (submissionPublisher == null) throw new MicroFoxException("No SSE connection has been established yet.");
        submissionPublisher.submit(sseObject);
    }

    @Override
    public void security(SecurityInfo securityInfo) {
        ResourceHolder.addSecurity(securityInfo);
    }

    @Override
    public void remove(String category) {
        ResourceHolder.listRoutes()
                .stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .forEach(item -> remove(item.getPath(), item.getHttpMethod()));
    }
}
