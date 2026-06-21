package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JmsConsumerController implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(JmsConsumerController.class);

    private final List<JMSContext> CONTEXTS = new CopyOnWriteArrayList<>();
    private final ConnectionFactory connectionFactory;
    private final int concurrency;
    private final String identity;

    private ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    public JmsConsumerController(JmsConnectionInfo info) {
        this.connectionFactory = info.getConnectionFactory();
        this.concurrency = info.getConcurrency();
        this.identity = info.getIdentity();
    }

    public void start(String destinationName, MessageListener listener, AckMode acknowledgeMode, DestinationType type) {
        if (running) return;
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "jms-consumer" + identity));

        for (int i = 0; i < concurrency; i++) {
            scheduler.scheduleWithFixedDelay(() -> ensureConsumerIsRunning(destinationName, acknowledgeMode, type, listener), 0, 5, TimeUnit.SECONDS);
        }
    }

    private void ensureConsumerIsRunning(String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener) {

        if (!running) return;

        try {
            if (isFull()) return;
            logger.info("Creating new JMS consumer for {}", identity);
            JMSContext context = connectionFactory.createContext(acknowledgeMode.getMode());
            CONTEXTS.add(context);
            context.setExceptionListener(ex -> handleException(context, ex));
            Destination destination = type.equals(DestinationType.TOPIC) ? context.createTopic(destinationName) : context.createQueue(destinationName);
            JMSConsumer consumer = context.createConsumer(destination);
            consumer.setMessageListener(listener);
            context.start();
            logger.info("JMS Consumer started successfully: {}", identity);
        } catch (Exception e) {
            logger.error("Failed to ensure JMS consumer is running for {}", identity, e);
        }
    }

    private boolean isFull() {
        return !CONTEXTS.isEmpty() && CONTEXTS.size() >= concurrency;
    }

    private void handleException(JMSContext context, JMSException ex) {
        logger.error("JMS Exception for consumer {} - {}", identity, ex.getMessage());
        removeContext(context);
    }

    private void removeContext(JMSContext context) {
        if (context != null) {
            JmsUtils.contextClose(context);
            CONTEXTS.remove(context);
        }
    }

    public void stop() {
        running = false;
        CONTEXTS.forEach(JMSContext::stop);
    }

    @Override
    public void close() {
        running = false;
        stop();
        CONTEXTS.forEach(JmsUtils::contextClose);
        CONTEXTS.clear();
        if (scheduler != null) scheduler.shutdownNow();
    }

    public String getIdentity() {
        return identity;
    }
}
