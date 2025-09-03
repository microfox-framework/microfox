package ir.moke.microfox.api.http;

import ir.moke.microfox.api.http.sse.SseObject;

import java.util.function.Supplier;

public interface HttpProvider {
    void filter(String path, Filter... filters);

    void http(String path, Method method, Route route);
    void websocket(Class<?> endpointClass);

    void sseRegister(String identity, String path);

    void ssePublisher(String identity, Supplier<SseObject> supplier);
}
