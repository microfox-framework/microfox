package ir.moke.microfox.httpclient;

import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.httpclient.ConnectionInfo;
import ir.moke.microfox.api.httpclient.HttpClientProvider;
import ir.moke.microfox.exception.MicroFoxException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HttpClientProviderImpl implements HttpClientProvider {
    @Override
    public void register(String identity, ConnectionInfo connectionInfo) {
        HttpClientRegistry.register(identity, connectionInfo);
    }

    @Override
    public void unregister(String identity) {
        HttpClientRegistry.unregister(identity);
    }

    @Override
    public <T> HttpResponse<T> send(String identity, HttpMethod method, Map<String, String> headers, HttpResponse.BodyHandler<T> bodyHandler, HttpRequest.BodyPublisher bodyPublisher) {
        try {
            HttpClient httpClient = HttpClientRegistry.getHttpClient(identity);
            ConnectionInfo connectionInfo = HttpClientRegistry.getConnectionInfo(identity);

            HttpRequest.Builder builder = HttpRequest.newBuilder();
            headers.forEach(builder::header);
            Optional.ofNullable(connectionInfo.requestTimeout()).ifPresent(builder::timeout);

            builder.uri(connectionInfo.uri());
            builder.method(method.name(), bodyPublisher);
            HttpRequest request = builder.build();
            return httpClient.send(request, bodyHandler);
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(String identity, HttpMethod method, Map<String, String> headers, HttpResponse.BodyHandler<T> bodyHandler, HttpRequest.BodyPublisher bodyPublisher) {
        try {
            HttpClient httpClient = HttpClientRegistry.getHttpClient(identity);
            ConnectionInfo connectionInfo = HttpClientRegistry.getConnectionInfo(identity);

            HttpRequest.Builder builder = HttpRequest.newBuilder();
            headers.forEach(builder::header);
            Optional.ofNullable(connectionInfo.requestTimeout()).ifPresent(builder::timeout);

            builder.uri(connectionInfo.uri());
            builder.method(method.name(), bodyPublisher);
            HttpRequest request = builder.build();
            return httpClient.sendAsync(request, bodyHandler);
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }

    }
}
