package ir.moke.microfox.ibmmq;

import com.ibm.msg.client.jakarta.jms.JmsConnectionFactory;
import com.ibm.msg.client.jakarta.jms.JmsFactoryFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import ir.moke.microfox.api.ibmmq.AcknowledgeType;
import jakarta.jms.*;

import java.lang.IllegalStateException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class IbmMQFactory {
    private static final Map<String, JmsConnectionFactory> QUEUE_MAP = new HashMap<>();

    public static void createConnectionFactory(String identity, String host, int port, String queueManager, String channel, String username, String password) throws JMSException {
        JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.JAKARTA_WMQ_PROVIDER);
        JmsConnectionFactory cf = ff.createConnectionFactory();

        // Set connection properties
        cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, host);
        cf.setIntProperty(WMQConstants.WMQ_PORT, port);
        cf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
        cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManager);
        cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "microfox-ibm-mq");
        if (username != null) cf.setStringProperty(WMQConstants.USERID, username);
        if (password != null) cf.setStringProperty(WMQConstants.PASSWORD, password);

        QUEUE_MAP.put(identity, cf);
    }

    public static void produceQueue(String identity, boolean transacted, AcknowledgeType type, Consumer<Session> consumer) {
        JmsConnectionFactory jmsConnectionFactory = getJmsConnectionFactory(identity);
        try (Connection connection = jmsConnectionFactory.createConnection()) {
            Session session = connection.createSession(transacted, type.getType());
            consumer.accept(session);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public static void produceTopic(String identity, boolean transacted, AcknowledgeType type, Consumer<Session> consumer) {
        JmsConnectionFactory jmsConnectionFactory = getJmsConnectionFactory(identity);
        try (Connection connection = jmsConnectionFactory.createConnection()) {
            Session session = connection.createSession(transacted, type.getType());
            consumer.accept(session);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public static void consumeQueue(String identity, String queueName, AcknowledgeType type, MessageListener listener) {
        JmsConnectionFactory jmsConnectionFactory = getJmsConnectionFactory(identity);
        try (JMSContext context = jmsConnectionFactory.createContext(type.getType())) {
            Destination destination = context.createQueue(queueName);
            JMSConsumer consumer = context.createConsumer(destination);
            consumer.setMessageListener(listener);
        }
    }

    public static void consumeTopic(String identity, String topicName, AcknowledgeType type, MessageListener listener) {
        JmsConnectionFactory jmsConnectionFactory = getJmsConnectionFactory(identity);
        try (JMSContext context = jmsConnectionFactory.createContext(type.getType())) {
            Destination destination = context.createTopic(topicName);
            JMSConsumer consumer = context.createConsumer(destination);
            consumer.setMessageListener(listener);
        }
    }

    private static JmsConnectionFactory getJmsConnectionFactory(String identity) {
        JmsConnectionFactory jmsConnectionFactory = QUEUE_MAP.get(identity);
        if (jmsConnectionFactory == null) {
            throw new IllegalStateException("No connection factory found for identity: " + identity);
        }
        return jmsConnectionFactory;
    }
}
