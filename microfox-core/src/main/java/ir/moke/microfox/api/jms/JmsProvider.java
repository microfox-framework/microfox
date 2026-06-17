package ir.moke.microfox.api.jms;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;

import java.util.function.Consumer;

public interface JmsProvider {
    void register(String identity, ConnectionFactory connectionFactory);

    void register(String identity, ConnectionFactory connectionFactory, int concurrency);

    void produce(String identity, Consumer<JMSContext> consumer);

    void consume(String identity, String queueName, AckMode acknowledgeMode, DestinationType type, MessageListener listener);

    void stop(String identity);
}
