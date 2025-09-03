package ir.moke.microfox.http;

import ir.moke.microfox.api.http.Filter;
import ir.moke.microfox.api.http.HttpProvider;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.exception.MicrofoxException;

import java.util.concurrent.SubmissionPublisher;
import java.util.function.Supplier;

public class HttpProviderImpl implements HttpProvider {

    @Override
    public void filter(String path, Filter... filters) {
        ResourceHolder.instance.addFilter(path, filters);
    }

    @Override
    public void http(String path, Method method, Route route) {
        ResourceHolder.instance.addRoute(method, path, route);
    }

    @Override
    public void websocket(Class<?> endpointClass) {
        ResourceHolder.instance.addWebsocket(endpointClass);
    }

    @Override
    public void sseRegister(String identity, String path) {
        ResourceHolder.instance.registerSse(identity, path);
    }

    @Override
    public void ssePublisher(String identity, Supplier<SseObject> supplier) {
        SubmissionPublisher<SseObject> submissionPublisher = ResourceHolder.instance.getSseByIdentity(identity);
        if (submissionPublisher == null) throw new MicrofoxException("No SSE connection has been established yet.");
        submissionPublisher.submit(supplier.get());
    }
}
