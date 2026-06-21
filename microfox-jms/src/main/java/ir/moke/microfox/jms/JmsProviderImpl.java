package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsConnectionInfo;
import ir.moke.microfox.api.jms.JmsProvider;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class JmsProviderImpl implements JmsProvider {
    private static final Logger logger = LoggerFactory.getLogger(JmsProviderImpl.class);
    private static final Set<JmsConsumerController> CONSUMER_CONTROLLERS = ConcurrentHashMap.newKeySet();
    private static final Map<String, JMSContext> PRODUCER_CONTEXT_MAP = new ConcurrentHashMap<>();

    @Override
    public void register(String identity, JmsConnectionInfo connectionInfo) {
        JmsFactory.register(identity, connectionInfo);
    }

    @Override
    public void unregister(String identity) {
        CONSUMER_CONTROLLERS.removeIf(controller -> {
            if (controller.getIdentity().equals(identity)) {
                controller.close();
                return true;
            }
            return false;
        });

        JMSContext context = PRODUCER_CONTEXT_MAP.remove(identity);
        if (context != null) JmsUtils.contextClose(context);
        JmsFactory.unregister(identity);
    }

    @Override
    public void produce(String identity, Consumer<JMSContext> contextConsumer) {
        try {
            JmsConnectionInfo info = JmsFactory.getConnectionInfo(identity);
            JMSContext jmsContext = PRODUCER_CONTEXT_MAP.computeIfAbsent(identity, _ -> info.getConnectionFactory().createContext());

            if (!isContextAlive(jmsContext)) {
                JMSContext oldContext = PRODUCER_CONTEXT_MAP.remove(identity);
                JmsUtils.contextClose(oldContext);
                jmsContext = info.getConnectionFactory().createContext();
                PRODUCER_CONTEXT_MAP.put(identity, jmsContext);
            }

            jmsContext.setExceptionListener(new JmsConsumerExceptionListener());
            contextConsumer.accept(jmsContext);
        } catch (Exception e) {
            logger.error("JMS producer exception for identity {}", identity, e);
            cleanupProducerContext(identity);
        }
    }

    private void cleanupProducerContext(String identity) {
        JMSContext context = PRODUCER_CONTEXT_MAP.remove(identity);
        if (context != null) JmsUtils.contextClose(context);
    }

    private static boolean isContextAlive(JMSContext context) {
        if (context == null) return false;
        try {
            context.getClientID();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void consume(String identity, String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener) {
        // Prevent duplicate controllers
        boolean alreadyExists = CONSUMER_CONTROLLERS.stream()
                .anyMatch(c -> c.getIdentity().equals(identity));

        if (alreadyExists) {
            logger.warn("Consumer for identity {} already exists", identity);
            return;
        }

        JmsConnectionInfo connectionInfo = JmsFactory.getConnectionInfo(identity);
        JmsConsumerController controller = new JmsConsumerController(connectionInfo);

        controller.start(destinationName, listener, acknowledgeMode, type);
        CONSUMER_CONTROLLERS.add(controller);

        logger.info("Registered consumer for identity: {}", identity);
    }

    @Override
    public void stop(String identity) {
        CONSUMER_CONTROLLERS.stream()
                .filter(c -> c.getIdentity().equals(identity))
                .forEach(JmsConsumerController::stop);
    }

    // Add this method for proper shutdown
    public void shutdown() {
        CONSUMER_CONTROLLERS.forEach(JmsConsumerController::close);
        CONSUMER_CONTROLLERS.clear();
        PRODUCER_CONTEXT_MAP.values().forEach(JmsUtils::contextClose);
        PRODUCER_CONTEXT_MAP.clear();
    }
}
