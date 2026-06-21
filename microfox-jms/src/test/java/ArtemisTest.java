import ch.qos.logback.classic.Level;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import ir.moke.utils.date.CalendarType;
import ir.moke.utils.date.DatePattern;
import ir.moke.utils.date.DateTimeUtils;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import java.time.ZonedDateTime;
import java.util.Locale;

import static ir.moke.microfox.MicroFox.*;

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
    private static final int CONNECTION_TTL = 5000; // 5 seconds

    static {
        MicroFox.logger(new ConsoleGenericModel("jms", "ir.moke.microfox.jms", Level.TRACE));
        registerArtemisConnectionFactory();
    }

    static void main() throws InterruptedException {
        jmsListener(IDENTITY, DestinationType.QUEUE, QUEUE_NAME, AckMode.AUTO_ACKNOWLEDGE, new CustomMessageListener());

//        while (true) {
//            sendTestMessage();
//            Thread.sleep(1000);
//        }

        int count = 0;
        for (; ; ) {
            sendTestMessage();
            count++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
            if (count == 10) {
                jmsStop(IDENTITY);
            }
        }
    }

    public static void sendTestMessage() {
        jmsProducer(IDENTITY, context -> {
            Queue destination = context.createQueue(QUEUE_NAME);
            JMSProducer producer = context.createProducer();
            String currentDateTime = DateTimeUtils.toString(ZonedDateTime.now(), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.DATE_TIME_PATTERN);
            TextMessage textMessage = context.createTextMessage(currentDateTime + " Hello consumer");
            producer.send(destination, textMessage);
            System.out.println("Message Successfully Send");
        });
    }

    public static void registerArtemisConnectionFactory() {
        String brokerUrl = "tcp://%s:%s".formatted(HOST, PORT);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connectionFactory.setUser(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.setConnectionTTL(CONNECTION_TTL);
        connectionFactory.setCallTimeout(10000);

        MicroFox.jmsRegister(IDENTITY, new JmsConnectionInfo(connectionFactory, 5));
    }
}
