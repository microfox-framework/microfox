package ir.moke.microfox.http;

import ir.moke.microfox.api.http.*;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicroFoxException;

import java.util.List;
import java.util.concurrent.SubmissionPublisher;

public class HttpProviderImpl implements HttpProvider {

    @Override
    public void filter(String path, int order, Filter filter) {
        ResourceHolder.addFilter(new FilterInfo(path, order, filter));
    }

    @Override
    public void filter(FilterInfo filterInfo) {
        ResourceHolder.addFilter(filterInfo);
    }

    @Override
    public void http(String path, HttpMethod httpMethod, Route route) {
        ResourceHolder.addRoute(new RouteInfo(path, httpMethod, route));
    }

    @Override
    public void http(String path, HttpMethod httpMethod, Route route, SecurityStrategy strategy, List<String> roles, List<String> scopes) {
        ResourceHolder.addRoute(new RouteInfo(path, httpMethod, route, strategy, roles, scopes));
    }

    @Override
    public void http(RouteInfo routeInfo) {
        ResourceHolder.addRoute(routeInfo);
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
}
