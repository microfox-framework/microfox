package ir.moke.microfox.elastic;

import java.time.Duration;

public record ElasticConfig(String host,
                            int port,
                            String username,
                            String password,
                            boolean useSSL,
                            Duration connectionTimeout) {
    public ElasticConfig(String host, int port, String username, String password, boolean useSSL) {
        this(host, port, username, password, useSSL, null);
    }

    public ElasticConfig(String host, int port, boolean useSSL) {
        this(host, port, null, null, useSSL, null);
    }

    public ElasticConfig(String host, int port, boolean useSSL, Duration connectionTimeout) {
        this(host, port, null, null, useSSL, connectionTimeout);
    }
}
