package ir.moke.microfox.elastic;

import ir.moke.microfox.exception.MicroFoxException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

public class ElasticHttpClient {

    public static String generateBaseURL(String identity) {
        ElasticConfig config = ElasticFactory.getConfig(identity);
        boolean useSSL = config.useSSL();
        String host = config.host();
        int port = config.port();
        return "%s://%s:%s".formatted(useSSL ? "https" : "http", host, port);
    }

    public static HttpResponse<String> post(String identity, String url, String json) {
        ElasticConfig config = ElasticFactory.getConfig(identity);
        String basicAuth = ElasticAuth.basic(config.username(), config.password());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(generateBaseURL(identity) + url))
                .header("Content-Type", "application/json")
                .header("Authorization", basicAuth)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try (HttpClient client = create(config)) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }
    }

    public static HttpResponse<String> put(String identity, String url, String json) {
        ElasticConfig config = ElasticFactory.getConfig(identity);
        String basicAuth = ElasticAuth.basic(config.username(), config.password());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(generateBaseURL(identity) + url))
                .header("Content-Type", "application/json")
                .header("Authorization", basicAuth)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try (HttpClient client = create(config)) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            return response;
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }
    }

    public static HttpResponse<String> get(String identity, String url) {
        ElasticConfig config = ElasticFactory.getConfig(identity);
        String basicAuth = ElasticAuth.basic(config.username(), config.password());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(generateBaseURL(identity) + url))
                .header("Authorization", basicAuth)
                .GET()
                .build();

        try (HttpClient client = create(config)) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }
    }

    public static HttpResponse<String> delete(String identity, String url) {
        ElasticConfig config = ElasticFactory.getConfig(identity);
        String basicAuth = ElasticAuth.basic(config.username(), config.password());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(generateBaseURL(identity) + url))
                .header("Authorization", basicAuth)
                .DELETE()
                .build();

        try (HttpClient client = create(config)) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }
    }

    public static HttpClient create(ElasticConfig config) {
        Duration duration = config.connectionTimeout();
        if (!config.useSSL()) {
            return HttpClient.newBuilder()
                    .connectTimeout(duration != null ? duration : Duration.ofSeconds(60))
                    .build();
        }

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(duration != null ? duration : Duration.ofSeconds(60))
                    .build();
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }
    }
}
