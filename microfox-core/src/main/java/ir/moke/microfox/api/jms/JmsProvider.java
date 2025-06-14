package ir.moke.microfox.api.jms;

import jakarta.jms.MessageListener;
import jakarta.jms.Session;

import java.util.function.Consumer;

public interface JmsProvider {

    void produceQueue(String identity, boolean transacted, int acknowledgeMode, Consumer<Session> consumer);

    void produceTopic(String identity, boolean transacted, int acknowledgeMode, Consumer<Session> consumer);

    void consumeQueue(String identity, String queueName, int acknowledgeMode, MessageListener listener);

    void consumeTopic(String identity, String topicName, int acknowledgeMode, MessageListener listener);
}
