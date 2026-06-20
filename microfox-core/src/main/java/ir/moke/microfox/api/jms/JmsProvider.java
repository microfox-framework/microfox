package ir.moke.microfox.api.jms;

import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;

import java.util.function.Consumer;

public interface JmsProvider {
    void register(String identity, JmsConnectionInfo connectionInfo);

    void unregister(String identity);

    void produce(String identity, Consumer<JMSContext> consumer);

    void consume(String identity, String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener);

    void stop(String identity);
}
