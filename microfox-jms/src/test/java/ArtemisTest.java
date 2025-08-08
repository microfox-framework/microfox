import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.jms.JmsFactory;
import ir.moke.microfox.utils.CalendarType;
import ir.moke.microfox.utils.DatePattern;
import ir.moke.microfox.utils.DateTimeUtils;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import java.time.ZonedDateTime;
import java.util.Locale;

import static ir.moke.microfox.MicroFox.jmsListener;
import static ir.moke.microfox.MicroFox.jmsProducer;

/**
 * Run artemis container with this command :
 * <p>
 * podman run -d --name artemis -e ARTEMIS_USER=admin -e ARTEMIS_PASSWORD=adminpass -p 61616:61616 -p 8161:8161 --rm apache/activemq-artemis:latest-alpine
 * </p>
 */
public class ArtemisTest {
    private static final String IDENTITY = "artemis";
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 61616;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "adminpass";
    private static final String QUEUE_NAME = "test";
    private static final int CONNECTION_TTL = 5000;

    static {
        registerArtemisConnectionFactory();
    }

    public static void main(String... str) {
        jmsListener(IDENTITY, DestinationType.QUEUE, QUEUE_NAME, AckMode.AUTO_ACKNOWLEDGE, new CustomMessageListener());
        sendTestMessage();
    }

    public static void sendTestMessage() {
        jmsProducer(IDENTITY, context -> {
            try {
                Queue destination = context.createQueue(QUEUE_NAME);
                JMSProducer producer = context.createProducer();
                String currentDateTime = DateTimeUtils.toString(ZonedDateTime.now(), Locale.ENGLISH, CalendarType.GREGORIAN, DatePattern.DATE_TIME_PATTERN);
                TextMessage textMessage = context.createTextMessage(currentDateTime + " Hello consumer");
                producer.send(destination, textMessage);
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
        JmsFactory.registerConnectionFactory(IDENTITY, connectionFactory, 10);
    }
}
