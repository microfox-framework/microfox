package ir.moke.microfox.jms;

import ir.moke.microfox.MicroFoxEnvironment;
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
    private static final Integer retryInterval = Integer.parseInt(MicroFoxEnvironment.getEnv("microfox.jms.connection.retry.interval"));
    private static final Set<JmsConsumerController> CONSUMER_CONTROLLERS = new HashSet<>();
    private static final Map<String, JMSContext> CONTEXT_MAP = new HashMap<>();

    @Override
    public void register(String identity, ConnectionFactory connectionFactory) {
        JmsFactory.register(identity, connectionFactory);
    }

    @Override
    public void register(String identity, ConnectionFactory connectionFactory, int concurrency) {
        JmsFactory.register(identity, connectionFactory, concurrency);
    }

    @Override
    public void produce(String identity, Consumer<JMSContext> contextConsumer) {
        JmsConnectionInfo connectionInfo = JmsFactory.getConnectionInfo(identity);
        JMSContext jmsContext = CONTEXT_MAP.computeIfAbsent(identity, _ -> connectionInfo.getConnectionFactory().createContext());
        contextConsumer.accept(jmsContext);
    }

    @Override
    public void consume(String identity, String queueName, AckMode acknowledgeMode, DestinationType type, MessageListener listener) {
        JmsConnectionInfo connectionInfo = JmsFactory.getConnectionInfo(identity);
        JmsConsumerController controller = new JmsConsumerController(connectionInfo);
        CONSUMER_CONTROLLERS.add(controller);
    }

    @Override
    public void stop(String identity) {

    }

}
