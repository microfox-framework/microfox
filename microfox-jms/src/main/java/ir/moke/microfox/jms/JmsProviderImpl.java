package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsProvider;
import ir.moke.microfox.exception.MicroFoxException;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class JmsProviderImpl implements JmsProvider {
    private static final Logger logger = LoggerFactory.getLogger(JmsProviderImpl.class);

    static {
        shutdownHook();
    }

    public void produce(String identity, Consumer<JMSContext> consumer) {
        ConnectionFactory connectionFactory = JmsFactory.getConnectionFactory(identity);
        try (JMSContext context = connectionFactory.createContext()) {
            consumer.accept(context);
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }
    }

    public void consume(String identity, String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener) {
        JmsConnectionInfo connectionInfo = JmsFactory.getConnectionInfo(identity);
        ConnectionFactory connectionFactory = connectionInfo.getConnectionFactory();
        int concurrency = connectionInfo.getConcurrency();

        try (ExecutorService es = Executors.newFixedThreadPool(concurrency, r -> new Thread(r,"jms-thread"))) {
            for (int i = 0; i < concurrency; i++) {
                es.execute(() -> consumeMessage(identity, destinationName, acknowledgeMode, type, listener, connectionFactory));
            }
        }
    }

    void consumeMessage(String identity, String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener, ConnectionFactory connectionFactory) {
        try {
            JMSContext context = connectionFactory.createContext(acknowledgeMode.getMode());
            Destination destination = type.equals(DestinationType.TOPIC) ? context.createTopic(destinationName) : context.createQueue(destinationName);
            JMSConsumer consumer = context.createConsumer(destination);
            consumer.setMessageListener(listener);
            context.setExceptionListener(new JmsExceptionHandler(identity));
            logger.info("JMS topic successfully registered {}", identity);

            // Block the thread, but do it properly
            CountDownLatch latch = new CountDownLatch(1);
            JmsFactory.registerContext(identity, connectionFactory, context, consumer, destinationName, acknowledgeMode, type, listener, latch);
            latch.await(); // block forever
        } catch (Exception e) {
            logger.error("JMS identity:{} destination:{} {}", destinationName, identity, e.getMessage());
            JmsFactory.closeContext(identity);
        }
    }

    private static void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.warn("Jms Shutdown hook triggered");
            JmsFactory.getInfoMap().keySet().forEach(JmsFactory::closeContext);
        }, "jms-shutdown-hook"));
    }
}
