package ir.moke.microfox.api.redis;

public record RedisConfig(String host,
                          Integer port,
                          String username,
                          String password,
                          String clientName,
                          Integer database,
                          Long timeout,
                          Boolean ssl,
                          Long reconnectDelay,
                          Integer computationThreadPool,
                          Integer ioThreadPool) {
    public RedisConfig(String host, Integer port) {
        this(host, port, null, null, null, null, null, null, null, null, null);
    }

    public RedisConfig(String host, Integer port, String username, String password) {
        this(host, port, username, password, null, 0, 0L, null, 0L, 0, 0);
    }

    public RedisConfig(String host, Integer port, String username, String password, Integer computationThreadPool, Integer ioThreadPool, Boolean ssl) {
        this(host, port, username, password, null, 0, 0L, ssl, 0L, computationThreadPool, ioThreadPool);
    }


}
