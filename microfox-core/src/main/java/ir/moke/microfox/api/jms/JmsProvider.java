package ir.moke.microfox.api.jms;

import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;

import java.util.function.Consumer;

public interface JmsProvider {

    void produce(String identity, Consumer<JMSContext> consumer);

    void consume(String identity, String queueName, AckMode acknowledgeMode, DestinationType type, MessageListener listener);
}
