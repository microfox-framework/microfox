import ir.moke.microfox.MicroFox;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.jms.JmsFactory;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
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
public class ArtemisTest {
    private static final String IDENTITY = "artemis";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 61616;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "adminpass";
    private static final String QUEUE_NAME = "test";
    private static final int CONNECTION_TTL = 100;

    @BeforeAll
    public static void init() {
        registerArtemisConnectionFactory();
    }

    @Test
    public void checkConsumer() {
        consumeQueue(IDENTITY, QUEUE_NAME, Session.AUTO_ACKNOWLEDGE, new CustomMessageListener());
        /*while (true) {
            try {
                Thread.sleep(1000);
                messageProducer();
            } catch (InterruptedException ignore) {
            }
        }*/
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

    public static void registerArtemisConnectionFactory() {
        String brokerUrl = "tcp://%s:%s".formatted(HOST, PORT);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setUser(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.setConnectionTTL(CONNECTION_TTL);
//        connectionFactory.setReconnectAttempts(-1); //auto reconnect
        JmsFactory.registerConnectionFactory(IDENTITY, connectionFactory);
    }
}
