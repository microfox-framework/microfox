package ir.moke.microfox.api.jms;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;

import java.util.function.Consumer;

public interface JmsProvider {
    void register(String identity, ConnectionFactory connectionFactory);

    void register(String identity, ConnectionFactory connectionFactory, int concurrency);

    void unregister(String identity);

    void produce(String identity, Consumer<JMSContext> consumer);

    void consume(String identity, String destinationName, AckMode acknowledgeMode, DestinationType type, MessageListener listener);

    void stop(String identity, String destinationName, DestinationType type);

    void stop(String identity);

    void stopAll();

    void close(String identity, String destinationName, DestinationType type);

    void close(String identity);

    void closeAll();

    void start(String identity, String destinationName, DestinationType type);
}
