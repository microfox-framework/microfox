package ir.moke.microfox.jms;

import ir.moke.microfox.exception.MicrofoxException;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;

import java.util.HashMap;
import java.util.Map;

public class JmsFactory {

    private static final Map<String, ConnectionFactory> FACTORY_MAP = new HashMap<>();
    private static final Map<String, JMSContext> CONTEXT_MAP = new HashMap<>();

    public static void registerConnectionFactory(String identity, ConnectionFactory connectionFactory) {
        if (FACTORY_MAP.containsKey(identity)) throw new MicrofoxException("JMS connection factory with identity %s already registered".formatted(identity));
        FACTORY_MAP.put(identity, connectionFactory);
    }

    public static ConnectionFactory getConnectionFactory(String identity) {
        ConnectionFactory connectionFactory = FACTORY_MAP.get(identity);
        if (connectionFactory == null) {
            throw new IllegalStateException("No connection factory found for identity: " + identity);
        }
        return connectionFactory;
    }

    public static void registerContext(String identity, JMSContext context) {
        CONTEXT_MAP.put(identity, context);
    }

    public static void closeContext(String identity) {
        JMSContext context = CONTEXT_MAP.remove(identity);
        if (context != null) context.close();
    }
}
