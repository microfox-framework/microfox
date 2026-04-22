package ir.moke.microfox.jms;

import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import ir.moke.microfox.api.jms.JmsProvider;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.IllegalStateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class JmsProviderImpl implements JmsProvider {
    private static final Logger logger = LoggerFactory.getLogger(JmsProviderImpl.class);
    private static final Integer retryInterval = Integer.parseInt(MicroFoxEnvironment.getEnv("microfox.jms.connection.retry.interval"));

    static {
        shutdownHook();
    }

    public void produce(String identity, Consumer<JMSContext> consumer) {
        JmsConnectionInfo connectionInfo = JmsFactory.getConnectionInfo(identity);
        if (connectionInfo == null) {
            throw new IllegalStateException("No connection factory found for identity: " + identity);
        }
        try {
            ConnectionFactory connectionFactory = connectionInfo.getConnectionFactory();
            JMSContext context = connectionInfo.getContext() == null ? connectionFactory.createContext() : connectionInfo.getContext();
            consumer.accept(context);
        } catch (Exception e) {
            logger.error("Failed to send message, {}", e.getMessage());
        }
    }

    public void consume(String identity,
                        String destinationName,
                        AckMode acknowledgeMode,
                        DestinationType type,
                        MessageListener listener) {

        JmsConnectionInfo info = JmsFactory.getConnectionInfo(identity);
        int concurrency = info.getConcurrency();
        ConnectionFactory connectionFactory = info.getConnectionFactory();


        final ExecutorService es = Executors.newFixedThreadPool(concurrency, r -> new Thread(r, "jms-consumer-" + identity));
        info.setExecutorService(es);
        for (int i = 0; i < concurrency; i++) {
            es.execute(() -> consumeMessage(identity, destinationName, acknowledgeMode, type, listener, connectionFactory));
        }
    }

    @Override
    public void stop(String identity) {
        JmsFactory.close(identity);
    }

    void consumeMessage(String identity,
                        String destinationName,
                        AckMode acknowledgeMode,
                        DestinationType type,
                        MessageListener listener,
                        ConnectionFactory connectionFactory) {
        try {
            JMSContext context = connectionFactory.createContext(acknowledgeMode.getMode());
            Destination destination = type == DestinationType.TOPIC ? context.createTopic(destinationName) : context.createQueue(destinationName);
            JMSConsumer consumer = context.createConsumer(destination);
            consumer.setMessageListener(listener);
            context.setExceptionListener(new JmsExceptionHandler(identity));
            JmsFactory.completeConnectionInfo(identity, connectionFactory, context, consumer, destinationName, acknowledgeMode, type, listener);
            logger.info("JMS [{}] consumer registered to {}", identity, destinationName);
            // only start after everything is ready
            context.start();
        } catch (Exception e) {
            logger.error("JMS identity:{} destination:{} {}", destinationName, identity, e.getMessage());
            JmsFactory.close(identity);
            delay();
            consumeMessage(identity, destinationName, acknowledgeMode, type, listener, connectionFactory);
        }
    }

    private static void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.warn("Jms Shutdown hook triggered");
            JmsFactory.getInfoMap().keySet().forEach(JmsFactory::close);
        }, "jms-shutdown-hook"));
    }

    private static void delay() {
        try {
            Thread.sleep(retryInterval);
        } catch (InterruptedException ignore) {
        }
    }
}
