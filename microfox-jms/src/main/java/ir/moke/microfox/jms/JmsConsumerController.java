package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JmsConsumerController {
    private static final Logger logger = LoggerFactory.getLogger(JmsConsumerController.class);
    private static final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "jms-consumer"));
    private final ConnectionFactory connectionFactory;
    private final int concurrency;
    private final String identity;
    private JMSContext context;
    private JMSConsumer consumer;
    private String destinationName;
    private DestinationType type;
    private Destination destination;

    public JmsConsumerController(JmsConnectionInfo info) {
        this.connectionFactory = info.getConnectionFactory();
        this.concurrency = info.getConcurrency();
        this.identity = info.getIdentity();
    }

    public void start(String destinationName, MessageListener listener, AckMode acknowledgeMode, DestinationType type) {
        this.destinationName = destinationName;
        this.type = type;
        for (int i = 0; i < concurrency; i++) {
            ses.scheduleWithFixedDelay(() -> consume(destinationName, acknowledgeMode, type, listener), 0, 2000, TimeUnit.SECONDS);
        }
    }

    private void consume(String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener) {
        createContext(acknowledgeMode);
        createDestination(destinationName, type);
        createConsumer(destination);
        consumer.setMessageListener(listener);
        context.start();
    }

    private void createContext(AckMode acknowledgeMode) {
        if (context == null) {
            context = connectionFactory.createContext(acknowledgeMode.getMode());
            context.setExceptionListener(new JmsConsumerExceptionListener());
        }
    }

    private void createConsumer(Destination destination) {
        if (this.consumer == null) {
            consumer = context.createConsumer(destination);
        }
    }

    private void createDestination(String destinationName, DestinationType type) {
        destination = type.equals(DestinationType.TOPIC) ? context.createTopic(this.destinationName) : context.createQueue(destinationName);
    }

    public void close() {
        if (this.context != null) {
            logger.info("Close consumer {} {}", type, destinationName);
            context.close();
        }
    }

    public void stop() {
        if (this.context != null) {
            logger.info("Stop consumer {} {}", type, destinationName);
            context.stop();
        }
    }

    public void start() {
        if (this.context != null) {
            logger.info("Start consumer {} {}", type, destinationName);
            context.start();
        }
    }

    public String getDestinationName() {
        return destinationName;
    }

    public DestinationType getType() {
        return type;
    }

    public String getIdentity() {
        return identity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JmsConsumerController that = (JmsConsumerController) o;
        return Objects.equals(destinationName, that.destinationName)
               && Objects.equals(identity, that.identity)
               && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, destinationName, type);
    }
}
