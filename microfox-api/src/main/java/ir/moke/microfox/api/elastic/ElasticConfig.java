package ir.moke.microfox.api.elastic;

public record ElasticConfig(String host,
                            int port,
                            String username,
                            String password,
                            boolean useSSL,
                            Integer connectionTimeout,
                            Integer requestTimeout) {
    public ElasticConfig(String host, int port, String username, String password, boolean useSSL) {
        this(host, port, username, password, useSSL, null, null);
    }

    public ElasticConfig(String host, int port, boolean useSSL) {
        this(host, port, null, null, useSSL, null, null);
    }

    public ElasticConfig(String host, int port, boolean useSSL, Integer connectionTimeout, Integer requestTimeout) {
        this(host, port, null, null, useSSL, connectionTimeout, requestTimeout);
    }
}
