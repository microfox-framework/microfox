import com.rabbitmq.jms.admin.RMQConnectionFactory;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.jms.JmsFactory;
import jakarta.jms.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static ir.moke.microfox.MicroFox.jmsListener;
import static ir.moke.microfox.MicroFox.jmsProducer;

/**
 * Run artemis container with this command :
 * <p>
 * podman run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:latest
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
        jmsListener(IDENTITY, QUEUE_NAME, DestinationType.QUEUE, Session.AUTO_ACKNOWLEDGE, new CustomMessageListener());
        sendTestMessage();
    }

    public static void sendTestMessage() {
        jmsProducer(IDENTITY, false, Session.AUTO_ACKNOWLEDGE, DestinationType.QUEUE, context -> {
            try {
                Queue destination = context.createQueue(QUEUE_NAME);
                JMSProducer producer = context.createProducer();
                TextMessage textMessage = context.createTextMessage(LocalDateTime.now() + " Hello consumer");
                producer.send(destination, textMessage);
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
