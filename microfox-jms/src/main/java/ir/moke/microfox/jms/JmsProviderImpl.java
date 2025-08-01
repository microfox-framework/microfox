package ir.moke.microfox.jms;

import ir.moke.microfox.MicrofoxEnvironment;
import ir.moke.microfox.api.jms.JmsProvider;
import ir.moke.microfox.exception.MicrofoxException;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class JmsProviderImpl implements JmsProvider {
    private static final Logger logger = LoggerFactory.getLogger(JmsProviderImpl.class);
    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();

    public void produceQueue(String identity, boolean transacted, int acknowledgeMode, Consumer<JMSContext> consumer) {
        ConnectionFactory connectionFactory = JmsFactory.getConnectionFactory(identity);
        try (JMSContext context = connectionFactory.createContext()) {
            consumer.accept(context);
        }
    }

    public void produceTopic(String identity, boolean transacted, int acknowledgeMode, Consumer<JMSContext> consumer) {
        ConnectionFactory connectionFactory = JmsFactory.getConnectionFactory(identity);
        try (JMSContext context = connectionFactory.createContext()) {
            consumer.accept(context);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }

    public void consumeQueue(String identity, String queueName, int acknowledgeMode, MessageListener listener) {
        reconnectScheduler.scheduleWithFixedDelay(() -> {
            try {
                if (JmsFactory.isConnected(identity)) return;
                ConnectionFactory connectionFactory = JmsFactory.getConnectionFactory(identity);
                JMSContext context = connectionFactory.createContext(acknowledgeMode);
                Destination destination = context.createQueue(queueName);
                JMSConsumer consumer = context.createConsumer(destination);
                consumer.setMessageListener(listener);
                context.setExceptionListener(new JmsExceptionHandler(identity));
                JmsFactory.registerContext(identity, new JmsConnectionInfo(context, consumer, true));
                logger.info("JMS queue successfully registered {}", identity);
            } catch (Exception e) {
                logger.error("JMS identity:{} queue:{} {}", identity, queueName, e.getMessage());
                JmsFactory.closeContext(identity);
            }
        }, 0, Long.parseLong(MicrofoxEnvironment.getEnv("MICROFOX_JMS_CONNECTION_RETRY_INTERVAL")), TimeUnit.MILLISECONDS);
    }

    public void consumeTopic(String identity, String topicName, int acknowledgeMode, MessageListener listener) {
        reconnectScheduler.scheduleWithFixedDelay(() -> {
            try {
                ConnectionFactory connectionFactory = JmsFactory.getConnectionFactory(identity);
                JMSContext context = connectionFactory.createContext(acknowledgeMode);
                Destination destination = context.createTopic(topicName);
                JMSConsumer consumer = context.createConsumer(destination);
                consumer.setMessageListener(listener);
                context.setExceptionListener(new JmsExceptionHandler(identity));
                JmsFactory.registerContext(identity, new JmsConnectionInfo(context, consumer, true));
                logger.info("JMS topic successfully registered {}", identity);
            } catch (Exception e) {
                logger.error("JMS identity:{} topic:{} {}", topicName, identity, e.getMessage());
                JmsFactory.closeContext(identity);
            }
        }, 0, Long.parseLong(MicrofoxEnvironment.getEnv("MICROFOX_JMS_CONNECTION_RETRY_INTERVAL")), TimeUnit.SECONDS);
    }
}
