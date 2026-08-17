package ir.moke.microfox.http;

import ir.moke.microfox.api.http.*;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.http.filter.CorsFilter;
import ir.moke.microfox.http.proxy.RestRouter;
import ir.moke.microfox.http.sse.SseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.SubmissionPublisher;

public class HttpProviderImpl implements HttpProvider {
    private static final Logger logger = LoggerFactory.getLogger(HttpProviderImpl.class);

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
            if (filterInfo != null) {
                filterInfo.setSort(i);
            } else {
                logger.warn("Filter with hash {} does not exists", hash);
            }
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
    public void rest(Class<?> restClass) {
        RestRouter.registerRoutes(restClass, this::route);
    }

    @Override
    public Set<RouteInfo> routeList() {
        return ResourceHolder.listRoutes();
    }

    @Override
    public void removeRoute(String path, HttpMethod method) {
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
    public void sseUnregister(String identity) {
        ResourceHolder.getSseInfoByIdentity(identity).ifPresent(SseInfo::close);
        ResourceHolder.removeSse(identity);
    }

    @Override
    public void ssePublisher(String identity, SseObject sseObject) {
        Optional<SseInfo> optSseInfo = ResourceHolder.getSseInfoByIdentity(identity);
        if (optSseInfo.isEmpty()) throw new MicroFoxException("No SSE connection has been established yet.");
        optSseInfo.get().getPublisher().submit(sseObject);
    }

    @Override
    public void security(SecurityInfo securityInfo) {
        ResourceHolder.addSecurity(securityInfo);
    }

    @Override
    public void removeRoute(String category) {
        ResourceHolder.listRoutes()
                .stream()
                .filter(item -> item.getCategory() != null)
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .forEach(item -> removeRoute(item.getPath(), item.getHttpMethod()));
    }

    @Override
    public void removeFilter(String category) {
        ResourceHolder.removeFilter(category);
    }

    @Override
    public void cors(String path, String name, Map<CORSHeader, String> valueMap) {
        filter(path, -800, name, "microfox", new CorsFilter(valueMap));
    }

    @Override
    public void corsAccessAll() {
        Map<CORSHeader, String> valueMap = new HashMap<>();
        valueMap.put(CORSHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        valueMap.put(CORSHeader.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET,PUT,DELETE,OPTIONS");
        valueMap.put(CORSHeader.ACCESS_CONTROL_ALLOW_HEADERS, "Accept, Content-Type, Authorization,accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        valueMap.put(CORSHeader.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        valueMap.put(CORSHeader.ACCESS_CONTROL_MAX_AGE, "86400");

        filter("/*", -800, "Wildcard CORS", "microfox", new CorsFilter(valueMap));
    }
}
