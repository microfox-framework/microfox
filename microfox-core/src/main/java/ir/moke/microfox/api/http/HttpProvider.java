package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.api.http.sse.SseSubscriber;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface HttpProvider {
    void filter(String path, Filter... filters);

    void get(String path, Route route);

    void post(String path, Route route);

    void delete(String path, Route route);

    void put(String path, Route route);

    void patch(String path, Route route);

    void head(String path, Route route);

    void options(String path, Route route);

    void trace(String path, Route route);

    void sseRegister(String identity, String path);

    void ssePublisher(String identity, Supplier<SseObject> supplier);
}
