package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import ir.moke.microfox.exception.MicroFoxException;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JmsFactory {
    private static final Logger logger = LoggerFactory.getLogger(JmsFactory.class);
    private static final Map<String, JmsConnectionInfo> INFO_MAP = new ConcurrentHashMap<>();

    public static void register(String identity, ConnectionFactory connectionFactory, int concurrency) {
        JmsConnectionInfo connectionInfo = new JmsConnectionInfo();
        connectionInfo.setConnectionFactory(connectionFactory);
        connectionInfo.setConcurrency(concurrency);
        if (INFO_MAP.containsKey(identity))
            throw new MicroFoxException("JMS connection factory with identity %s already registered".formatted(identity));
        INFO_MAP.put(identity, connectionInfo);
        logger.info("Jms with identity {} registered", identity);
    }

    public static void register(String identity, ConnectionFactory connectionFactory) {
        register(identity, connectionFactory, 1);
    }

    static void completeConnectionInfo(String identity,
                                       ConnectionFactory connectionFactory,
                                       JMSContext context,
                                       JMSConsumer consumer,
                                       String destinationName,
                                       AckMode ackMode,
                                       DestinationType type,
                                       MessageListener listener) {
        JmsConnectionInfo info = INFO_MAP.get(identity);
        info.setIdentity(identity);
        info.setConnectionFactory(connectionFactory);
        info.setContext(context);
        info.setConsumer(consumer);
        info.setDestination(destinationName);
        info.setMode(ackMode);
        info.setType(type);
        info.setListener(listener);
        info.setConnected(true);
    }

    static JmsConnectionInfo close(String identity) {
        JmsConnectionInfo info = INFO_MAP.get(identity);
        if (info == null) return null;

        info.setConnected(false);

        try {
            if (info.getConsumer() != null) info.getConsumer().close();
        } catch (Exception ignored) {
        }

        try {
            if (info.getContext() != null) info.getContext().close();
        } catch (Exception ignored) {
        }

        try {
            if (info.getExecutorService() != null) info.getExecutorService().close();
        } catch (Exception ignored) {
        }

        info.setContext(null);
        info.setConsumer(null);
        info.setExecutorService(null);

        logger.info("JMS {} context closed", identity);
        return info;
    }


    public static boolean isConnected(String identity) {
        return INFO_MAP.getOrDefault(identity, new JmsConnectionInfo()).isConnected();
    }

    public static JmsConnectionInfo getConnectionInfo(String identity) {
        return INFO_MAP.get(identity);
    }

    static Map<String, JmsConnectionInfo> getInfoMap() {
        return INFO_MAP;
    }
}
