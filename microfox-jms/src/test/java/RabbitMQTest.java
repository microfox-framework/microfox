import com.rabbitmq.jms.admin.RMQConnectionFactory;
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
public class RabbitMQTest {
    private static final String IDENTITY = "rabbitmq";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5672;
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "guest";
    private static final String VIRTUAL_HOST = "/";
    private static final String QUEUE_NAME = "test";

    @BeforeAll
    public static void init() {
        registerRabbitMQConnectionFactory();
    }

    @Test
    public void checkConsumer() {
        consumeQueue(IDENTITY, QUEUE_NAME, Session.AUTO_ACKNOWLEDGE, new CustomMessageListener());
        messageProducer();
    }

    @AfterAll
    public static void shutdown() {
        System.exit(0);
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

    public static void registerRabbitMQConnectionFactory() {
        try {
            RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
            connectionFactory.setHost(HOST);
            connectionFactory.setPort(PORT);
            connectionFactory.setUsername(USERNAME);
            connectionFactory.setPassword(PASSWORD);
            connectionFactory.setVirtualHost(VIRTUAL_HOST);
            JmsFactory.registerConnectionFactory(IDENTITY, connectionFactory);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }
}
