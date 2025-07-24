package ir.moke.microfox.api.jms;

import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;

import java.util.function.Consumer;

public interface JmsProvider {

    void produceQueue(String identity, boolean transacted, int acknowledgeMode, Consumer<JMSContext> consumer);

    void produceTopic(String identity, boolean transacted, int acknowledgeMode, Consumer<JMSContext> consumer);

    void consumeQueue(String identity, String queueName, int acknowledgeMode, MessageListener listener);

    void consumeTopic(String identity, String topicName, int acknowledgeMode, MessageListener listener);
}
