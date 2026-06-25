import ch.qos.logback.classic.Level;
import com.rabbitmq.jms.admin.RMQConnectionFactory;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.MicrofoxRegistry;
import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import ir.moke.utils.date.CalendarType;
import ir.moke.utils.date.DatePattern;
import ir.moke.utils.date.DateTimeUtils;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;

import java.time.ZonedDateTime;
import java.util.Locale;

import static ir.moke.microfox.MicroFox.*;

/**
 * Run rabbitmq container with this command :
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

    static {
        MicroFox.logger(new ConsoleGenericModel("jms", "ir.moke.microfox.jms", Level.TRACE));
        registerRabbitMQConnectionFactory();
    }


    static void main() {
        jmsListener(IDENTITY, DestinationType.QUEUE, QUEUE_NAME, AckMode.AUTO_ACKNOWLEDGE, new CustomMessageListener());

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
            try {
                Queue destination = context.createQueue(QUEUE_NAME);
                JMSProducer producer = context.createProducer();
                String currentDateTime = DateTimeUtils.toString(ZonedDateTime.now(), Locale.ENGLISH, CalendarType.PERSIAN, DatePattern.DATE_TIME_PATTERN);
                TextMessage textMessage = context.createTextMessage(currentDateTime + " Hello consumer");
                producer.send(destination, textMessage);
            } catch (Exception e) {
                throw new MicroFoxException(e);
            }
        });
    }

    public static void registerRabbitMQConnectionFactory() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.setVirtualHost(VIRTUAL_HOST);

        MicrofoxRegistry.jmsRegister(IDENTITY, new JmsConnectionInfo(connectionFactory));
    }
}
