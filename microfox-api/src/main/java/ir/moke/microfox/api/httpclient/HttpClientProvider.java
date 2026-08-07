package ir.moke.microfox.api.httpclient;

import ir.moke.microfox.api.http.HttpMethod;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface HttpClientProvider {

    void register(String identity, ConnectionInfo connectionInfo);

    void unregister(String identity);

    <T> HttpResponse<T> send(String identity, HttpMethod method, Map<String, String> headers, HttpResponse.BodyHandler<T> responseBodyHandler, HttpRequest.BodyPublisher bodyPublisher);

    <T> CompletableFuture<HttpResponse<T>> sendAsync(String identity, HttpMethod method, Map<String, String> headers, HttpResponse.BodyHandler<T> responseBodyHandler, HttpRequest.BodyPublisher bodyPublisher);
}
