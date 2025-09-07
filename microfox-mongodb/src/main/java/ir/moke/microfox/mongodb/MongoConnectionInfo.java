package ir.moke.microfox.mongodb;

public class MongoConnectionInfo {
    private String username;
    private String password;
    private String host;
    private int port;
    private String databaseName;
    private String connectionMetadata = "";
    private int poolMax = 32;
    private int poolMin = 10;
    private int poolMaxWaitTime = 5;
    private int poolMaxConnectionIdleTime = 60;

    public MongoConnectionInfo(String username, String password, String host, int port, String databaseName) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
    }

    public MongoConnectionInfo(String username, String password, String host, int port, String databaseName, String connectionMetadata) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.connectionMetadata = connectionMetadata;
    }

    public MongoConnectionInfo(String username, String password, String host, int port, String databaseName, String connectionMetadata, int poolMax, int poolMin, int poolMaxWaitTime, int poolMaxConnectionIdleTime) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.connectionMetadata = connectionMetadata;
        this.poolMax = poolMax;
        this.poolMin = poolMin;
        this.poolMaxWaitTime = poolMaxWaitTime;
        this.poolMaxConnectionIdleTime = poolMaxConnectionIdleTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getConnectionMetadata() {
        return connectionMetadata;
    }

    public void setConnectionMetadata(String connectionMetadata) {
        this.connectionMetadata = connectionMetadata;
    }

    public int getPoolMax() {
        return poolMax;
    }

    public void setPoolMax(int poolMax) {
        this.poolMax = poolMax;
    }

    public int getPoolMin() {
        return poolMin;
    }

    public void setPoolMin(int poolMin) {
        this.poolMin = poolMin;
    }

    public int getPoolMaxWaitTime() {
        return poolMaxWaitTime;
    }

    public void setPoolMaxWaitTime(int poolMaxWaitTime) {
        this.poolMaxWaitTime = poolMaxWaitTime;
    }

    public int getPoolMaxConnectionIdleTime() {
        return poolMaxConnectionIdleTime;
    }

    public void setPoolMaxConnectionIdleTime(int poolMaxConnectionIdleTime) {
        this.poolMaxConnectionIdleTime = poolMaxConnectionIdleTime;
    }
}
