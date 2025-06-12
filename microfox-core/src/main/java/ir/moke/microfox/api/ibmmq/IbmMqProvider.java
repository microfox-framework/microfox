package ir.moke.microfox.api.ibmmq;

import jakarta.jms.MessageListener;
import jakarta.jms.Session;

import java.util.function.Consumer;

public interface IbmMqProvider {
    void ibmMQConsumeQueue(String identity, String queue, AcknowledgeType type, MessageListener listener);

    void ibmMQConsumeTopic(String identity, String topic, AcknowledgeType type, MessageListener listener);

    void ibmMQProducerQueue(String identity, AcknowledgeType type, Consumer<Session> consumer);
    void ibmMQProducerQueueTx(String identity, AcknowledgeType type, Consumer<Session> consumer);

    void ibmMQProducerTopic(String identity, AcknowledgeType type, Consumer<Session> consumer);
    void ibmMQProducerTopicTx(String identity, AcknowledgeType type, Consumer<Session> consumer);
}
