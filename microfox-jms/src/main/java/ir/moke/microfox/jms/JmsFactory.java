package ir.moke.microfox.jms;

import ir.moke.microfox.exception.MicrofoxException;
import jakarta.jms.ConnectionFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JmsFactory {

    private static final Map<String, ConnectionFactory> FACTORY_MAP = new ConcurrentHashMap<>();
    private static final Map<String, JmsConnectionInfo> INFO_MAP = new ConcurrentHashMap<>();

    public static void registerConnectionFactory(String identity, ConnectionFactory connectionFactory) {
        if (FACTORY_MAP.containsKey(identity))
            throw new MicrofoxException("JMS connection factory with identity %s already registered".formatted(identity));
        FACTORY_MAP.put(identity, connectionFactory);
    }

    public static ConnectionFactory getConnectionFactory(String identity) {
        ConnectionFactory connectionFactory = FACTORY_MAP.get(identity);
        if (connectionFactory == null) {
            throw new IllegalStateException("No connection factory found for identity: " + identity);
        }
        return connectionFactory;
    }

    public static void registerContext(String identity, JmsConnectionInfo info) {
        INFO_MAP.put(identity, info);
    }

    public static void closeContext(String identity) {
        JmsConnectionInfo inf = INFO_MAP.get(identity);
        if (inf == null) return;
        try {
            if (inf.getConsumer() != null) inf.getConsumer().close();
            if (inf.getContext() != null) inf.getContext().close();
        } catch (Exception ignore) {
        } finally {
            INFO_MAP.remove(identity);
        }
    }

    public static boolean isConnected(String identity) {
        return INFO_MAP.getOrDefault(identity, new JmsConnectionInfo(false)).isConnected();
    }

    public static JmsConnectionInfo getConnectionInfo(String identity) {
        return INFO_MAP.get(identity);
    }
}
