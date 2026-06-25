package ir.moke.microfox.utils;

import ir.moke.kafir.http.Interceptor;

import javax.net.ssl.SSLContext;
import java.net.Authenticator;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class HttpClientConfig {
    private final String baseUri;
    private final HttpClient.Version version;
    private final Authenticator authenticator;
    private final ExecutorService executorService;
    private final SSLContext sslContext;
    private final Duration connectionTimeout;
    private final Map<String, String> headers;
    private final Interceptor interceptor;

    private HttpClientConfig(Builder builder) {
        this.baseUri = builder.baseUri;
        this.version = builder.version;
        this.authenticator = builder.authenticator;
        this.executorService = builder.executorService;
        this.sslContext = builder.sslContext;
        this.connectionTimeout = builder.connectionTimeout;
        this.headers = builder.headers;
        this.interceptor = builder.interceptor;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public HttpClient.Version getVersion() {
        return version;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public static class Builder {
        private String baseUri;
        private Map<String, String> headers;
        private Interceptor interceptor;
        private HttpClient.Version version;
        private Authenticator authenticator;
        private ExecutorService executorService;
        private SSLContext sslContext;
        private Duration connectionTimeout;

        public Builder setBaseUri(String baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public HttpClient.Version getVersion() {
            return version;
        }

        public Builder setVersion(HttpClient.Version version) {
            this.version = version;
            return this;
        }

        public Builder setAuthenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        public Builder setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder setSslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public Builder setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setInterceptor(Interceptor interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public HttpClientConfig build() {
            return new HttpClientConfig(this);
        }
    }
}
