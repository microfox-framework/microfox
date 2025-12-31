package ir.moke.microfox.http;

import ir.moke.microfox.api.http.*;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicrofoxException;

import java.util.concurrent.SubmissionPublisher;

public class HttpProviderImpl implements HttpProvider {

    @Override
    public void filter(String path, Filter... filters) {
        ResourceHolder.addFilter(path, filters);
    }

    @Override
    public void http(String path, Method method, Route route) {
        ResourceHolder.addRoute(method, path, route);
    }

    @Override
    public void http(RouteInfo routeInfo) {
        ResourceHolder.addRoute(routeInfo.method(), routeInfo.path(), routeInfo.route());
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
        if (submissionPublisher == null) throw new MicrofoxException("No SSE connection has been established yet.");
        submissionPublisher.submit(sseObject);
    }
}
