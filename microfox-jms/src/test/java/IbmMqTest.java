import com.ibm.msg.client.jakarta.jms.JmsConnectionFactory;
import com.ibm.msg.client.jakarta.jms.JmsFactoryFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.jms.JmsFactory;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static ir.moke.microfox.MicroFox.consumeQueue;

/**
 * Run artemis container with this command :
 * <p>
 * podman run -d --name artemis -e ARTEMIS_USER=admin -e ARTEMIS_PASSWORD=adminpass -p 61616:61616 -p 8161:8161 --rm apache/activemq-artemis:latest-al
 * pine
 * </p>
 */
public class IbmMqTest {
    private static final String IDENTITY = "ibm-mq";
    private static final String APPLICATION_NAME = "microfox-ibm-mq";
    private static final String CHANNEL_NAME = "DEV.ADMIN.SVRCONN";
    private static final String QUEUE_MANAGER = "QM1";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1414;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "adminpass";
    private static final String CONNECTION_TTL = "3600";
    private static final String QUEUE_NAME = "DEV.QUEUE.1";

    @BeforeAll
    public static void init() {
        registerIBMConnectionFactory();
    }

    @Test
    public void checkConsumer() {
        consumeQueue(IDENTITY, QUEUE_NAME, Session.AUTO_ACKNOWLEDGE, new CustomMessageListener());
        messageProducer();
    }

    public static void messageProducer() {
        MicroFox.producerQueue(IDENTITY, false, Session.AUTO_ACKNOWLEDGE, session -> {
            try {
                Queue destination = session.createQueue(QUEUE_NAME);
                MessageProducer messageProducer = session.createProducer(destination);
                TextMessage textMessage = session.createTextMessage(LocalDateTime.now() + " Hello consumer");
                messageProducer.send(textMessage);
            } catch (Exception e) {
                throw new MicrofoxException(e);
            }
        });
    }

    public static void registerIBMConnectionFactory() {
        try {
            JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.JAKARTA_WMQ_PROVIDER);
            JmsConnectionFactory connectionFactory = ff.createConnectionFactory();

            // Set connection properties
            connectionFactory.setStringProperty(WMQConstants.WMQ_HOST_NAME, HOST);
            connectionFactory.setIntProperty(WMQConstants.WMQ_PORT, PORT);
            connectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, CHANNEL_NAME);
            connectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, QUEUE_MANAGER);
            connectionFactory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, APPLICATION_NAME);
            connectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            connectionFactory.setStringProperty(WMQConstants.USERID, USERNAME);
            connectionFactory.setStringProperty(WMQConstants.PASSWORD, PASSWORD);
            connectionFactory.setStringProperty(WMQConstants.TIME_TO_LIVE, CONNECTION_TTL);
            JmsFactory.registerConnectionFactory(IDENTITY, connectionFactory);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }
}
