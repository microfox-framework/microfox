package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsProvider;
import ir.moke.microfox.exception.MicrofoxException;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class JmsProviderImpl implements JmsProvider {
    private static final Logger logger = LoggerFactory.getLogger(JmsProviderImpl.class);
    private static final int DEFAULT_CONCURRENCY = 1;
    private static final int MAX_CONCURRENCY = 1;
    private static final int KEEP_ALIVE_TIMEOUT = 3600;
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(DEFAULT_CONCURRENCY, MAX_CONCURRENCY, KEEP_ALIVE_TIMEOUT, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        shutdownHook();
    }

    public void produce(String identity, Consumer<JMSContext> consumer) {
        ConnectionFactory connectionFactory = JmsFactory.getConnectionFactory(identity);
        try (JMSContext context = connectionFactory.createContext()) {
            consumer.accept(context);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }

    public void consume(String identity, String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener) {
        JmsConnectionInfo connectionInfo = JmsFactory.getConnectionInfo(identity);
        ConnectionFactory connectionFactory = connectionInfo.getConnectionFactory();
        int concurrency = connectionInfo.getConcurrency();
        int maxConcurrency = connectionInfo.getMaxConcurrency();
        int keepAliveTimeout = connectionInfo.getKeepAliveTimeout();

        threadPoolExecutor.setMaximumPoolSize(maxConcurrency);
        threadPoolExecutor.setCorePoolSize(concurrency);
        threadPoolExecutor.setKeepAliveTime(keepAliveTimeout, TimeUnit.SECONDS);

        for (int i = 0; i < concurrency; i++) {
            threadPoolExecutor.submit(() -> consumeMessage(identity, destinationName, acknowledgeMode, type, listener, connectionFactory));
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
