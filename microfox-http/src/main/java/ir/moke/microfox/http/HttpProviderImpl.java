package ir.moke.microfox.http;

import ir.moke.microfox.api.http.Filter;
import ir.moke.microfox.api.http.HttpProvider;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.api.http.sse.SseSubscriber;
import ir.moke.microfox.exception.MicrofoxException;

import java.util.concurrent.SubmissionPublisher;
import java.util.function.Supplier;

public class HttpProviderImpl implements HttpProvider {

    @Override
    public void filter(String path, Filter... filters) {
        ResourceHolder.instance.addFilter(path, filters);
    }

    @Override
    public void get(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.GET, path, route);
    }

    @Override
    public void post(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.POST, path, route);
    }

    @Override
    public void delete(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.DELETE, path, route);
    }

    @Override
    public void put(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.PUT, path, route);
    }

    @Override
    public void patch(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.PATCH, path, route);
    }

    @Override
    public void head(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.HEAD, path, route);
    }

    @Override
    public void options(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.OPTIONS, path, route);
    }

    @Override
    public void trace(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.TRACE, path, route);
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
