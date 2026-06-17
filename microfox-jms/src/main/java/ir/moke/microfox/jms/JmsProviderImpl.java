package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import ir.moke.microfox.api.jms.JmsProvider;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class JmsProviderImpl implements JmsProvider {
    private static final Logger logger = LoggerFactory.getLogger(JmsProviderImpl.class);
    private static final Set<JmsConsumerController> CONSUMER_CONTROLLERS = new HashSet<>();
    private static final Map<String, JMSContext> PRODUCER_CONTEXT_MAP = new HashMap<>();

    @Override
    public void register(String identity, ConnectionFactory connectionFactory) {
        JmsFactory.register(identity, connectionFactory);
    }

    @Override
    public void register(String identity, ConnectionFactory connectionFactory, int concurrency) {
        JmsFactory.register(identity, connectionFactory, concurrency);
    }

    @Override
    public void unregister(String identity) {
        try (JMSContext context = PRODUCER_CONTEXT_MAP.remove(identity)) {
            JmsFactory.unregister(identity);
        }
    }

    @Override
    public void produce(String identity, Consumer<JMSContext> contextConsumer) {
        try {
            logger.trace("JMS producer called on identity {}", identity);
            JmsConnectionInfo connectionInfo = JmsFactory.getConnectionInfo(identity);
            JMSContext jmsContext = PRODUCER_CONTEXT_MAP.computeIfAbsent(identity, _ -> connectionInfo.getConnectionFactory().createContext());
            jmsContext.setExceptionListener(new JmsConsumerExceptionListener());
            contextConsumer.accept(jmsContext);
        } catch (Exception e) {
            logger.error("Jms producer exception - {}", e.getMessage());
            JMSContext context = PRODUCER_CONTEXT_MAP.remove(identity);
            if (context != null) context.close();
        }
    }

    @Override
    public void consume(String identity, String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener) {
        JmsConnectionInfo connectionInfo = JmsFactory.getConnectionInfo(identity);
        JmsConsumerController controller = new JmsConsumerController(connectionInfo);
        controller.start(destinationName, listener, acknowledgeMode, type);
        CONSUMER_CONTROLLERS.add(controller);
    }

    @Override
    public void stop(String identity, String destinationName, DestinationType type) {
        CONSUMER_CONTROLLERS.stream()
                .filter(item -> item.getIdentity().equals(identity))
                .filter(item -> item.getDestinationName().equals(destinationName))
                .filter(item -> item.getType().equals(type))
                .findFirst()
                .ifPresent(JmsConsumerController::stop);
    }

    @Override
    public void stop(String identity) {
        CONSUMER_CONTROLLERS.stream()
                .filter(item -> item.getIdentity().equals(identity))
                .findFirst()
                .ifPresent(JmsConsumerController::stop);
    }

    @Override
    public void stopAll() {
        CONSUMER_CONTROLLERS.forEach(JmsConsumerController::stop);
    }

    @Override
    public void close(String identity, String destinationName, DestinationType type) {
        CONSUMER_CONTROLLERS.stream()
                .filter(item -> item.getIdentity().equals(identity))
                .filter(item -> item.getDestinationName().equals(destinationName))
                .filter(item -> item.getType().equals(type))
                .findFirst()
                .ifPresent(JmsConsumerController::close);
    }

    @Override
    public void close(String identity) {
        CONSUMER_CONTROLLERS.stream()
                .filter(item -> item.getIdentity().equals(identity))
                .findFirst()
                .ifPresent(JmsConsumerController::close);
    }

    @Override
    public void closeAll() {
        CONSUMER_CONTROLLERS.forEach(JmsConsumerController::close);
    }

    @Override
    public void start(String identity, String destinationName, DestinationType type) {
        CONSUMER_CONTROLLERS.stream()
                .filter(item -> item.getIdentity().equals(identity))
                .filter(item -> item.getDestinationName().equals(destinationName))
                .filter(item -> item.getType().equals(type))
                .findFirst()
                .ifPresent(JmsConsumerController::start);
    }
}
