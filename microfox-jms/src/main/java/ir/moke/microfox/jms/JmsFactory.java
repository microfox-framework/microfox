package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.exception.MicrofoxException;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class JmsFactory {
    private static final Logger logger = LoggerFactory.getLogger(JmsFactory.class);
    private static final Map<String, JmsConnectionInfo> INFO_MAP = new ConcurrentHashMap<>();

    public static void registerConnectionFactory(String identity, ConnectionFactory connectionFactory) {
        JmsConnectionInfo connectionInfo = new JmsConnectionInfo();
        connectionInfo.setConnectionFactory(connectionFactory);
        if (INFO_MAP.containsKey(identity))
            throw new MicrofoxException("JMS connection factory with identity %s already registered".formatted(identity));
        INFO_MAP.put(identity, connectionInfo);
        logger.info("Jms with identity {} registered", identity);
    }

    public static void registerConnectionFactory(String identity, ConnectionFactory connectionFactory, int concurrency, int maxConcurrency, int keepAliveTimeout) {
        JmsConnectionInfo connectionInfo = new JmsConnectionInfo();
        connectionInfo.setConnectionFactory(connectionFactory);
        connectionInfo.setConcurrency(concurrency);
        connectionInfo.setMaxConcurrency(maxConcurrency);
        connectionInfo.setKeepAliveTimeout(keepAliveTimeout);
        if (INFO_MAP.containsKey(identity))
            throw new MicrofoxException("JMS connection factory with identity %s already registered".formatted(identity));
        INFO_MAP.put(identity, connectionInfo);
        logger.info("Jms with identity {} registered", identity);
    }

    static ConnectionFactory getConnectionFactory(String identity) {
        JmsConnectionInfo connectionInfo = INFO_MAP.get(identity);
        if (connectionInfo.getConnectionFactory() == null) {
            throw new IllegalStateException("No connection factory found for identity: " + identity);
        }
        return connectionInfo.getConnectionFactory();
    }

    static void registerContext(String identity,
                                ConnectionFactory connectionFactory,
                                JMSContext context,
                                JMSConsumer consumer,
                                String destinationName,
                                AckMode ackMode,
                                DestinationType type,
                                MessageListener listener,
                                CountDownLatch latch) {
        JmsConnectionInfo info = INFO_MAP.get(identity);
        info.setConnectionFactory(connectionFactory);
        info.setContext(context);
        info.setConsumer(consumer);
        info.setDestination(destinationName);
        info.setMode(ackMode);
        info.setType(type);
        info.setListener(listener);
        info.setLatch(latch);
    }

    static JmsConnectionInfo closeContext(String identity) {
        JmsConnectionInfo inf = INFO_MAP.get(identity);
        if (inf == null) return null;

        inf.setConnected(false);
        if (inf.getConsumer() != null) inf.getConsumer().close();
        if (inf.getContext() != null) inf.getContext().close();
        if (inf.getLatch() != null) inf.getLatch().countDown();

        logger.info("Jms context with identity {} closed", identity);
        return inf;
    }

    public static boolean isConnected(String identity) {
        return INFO_MAP.getOrDefault(identity, new JmsConnectionInfo()).isConnected();
    }

    static JmsConnectionInfo getConnectionInfo(String identity) {
        return INFO_MAP.get(identity);
    }

    static Map<String, JmsConnectionInfo> getInfoMap() {
        return INFO_MAP;
    }
}
