package ir.moke.microfox.httpclient;

import ir.moke.microfox.api.httpclient.ConnectionInfo;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpClientRegistry {
    private static final Map<String, HttpClient> CLIENT_MAP = new HashMap<>();
    private static final Map<String, ConnectionInfo> CONNECTION_INFO_MAP = new HashMap<>();

    public static void register(String identity, ConnectionInfo connectionInfo) {
        HttpClient.Builder builder = HttpClient.newBuilder();
        Optional.ofNullable(connectionInfo.version()).ifPresent(builder::version);
        Optional.ofNullable(connectionInfo.tcpTimeout()).ifPresent(builder::connectTimeout);
        HttpClient client = builder.build();
        CLIENT_MAP.put(identity, client);
        CONNECTION_INFO_MAP.put(identity, connectionInfo);
    }

    public static HttpClient getHttpClient(String identity) {
        return CLIENT_MAP.get(identity);
    }

    public static ConnectionInfo getConnectionInfo(String identity) {
        return CONNECTION_INFO_MAP.get(identity);
    }

    public static void unregister(String identity) {
        HttpClient client = CLIENT_MAP.remove(identity);
        if (client != null) client.close();

        CONNECTION_INFO_MAP.remove(identity);
    }
}
