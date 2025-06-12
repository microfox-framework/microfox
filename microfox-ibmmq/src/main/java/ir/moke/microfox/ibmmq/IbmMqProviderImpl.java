package ir.moke.microfox.ibmmq;

import ir.moke.microfox.api.ibmmq.AcknowledgeType;
import ir.moke.microfox.api.ibmmq.IbmMqProvider;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;

import java.util.function.Consumer;

public class IbmMqProviderImpl implements IbmMqProvider {

    @Override
    public void ibmMQConsumeQueue(String identity, String queue, AcknowledgeType type, MessageListener listener) {
        IbmMQFactory.consumeQueue(identity, queue, type, listener);
    }

    @Override
    public void ibmMQConsumeTopic(String identity, String topic, AcknowledgeType type, MessageListener listener) {
        IbmMQFactory.consumeTopic(identity, topic, type, listener);
    }

    @Override
    public void ibmMQProducerQueue(String identity, AcknowledgeType type, Consumer<Session> consumer) {
        IbmMQFactory.produceQueue(identity, false, type, consumer);
    }

    @Override
    public void ibmMQProducerQueueTx(String identity, AcknowledgeType type, Consumer<Session> consumer) {
        IbmMQFactory.produceQueue(identity, true, type, consumer);
    }

    @Override
    public void ibmMQProducerTopic(String identity, AcknowledgeType type, Consumer<Session> consumer) {
        IbmMQFactory.produceTopic(identity, false, type, consumer);
    }

    @Override
    public void ibmMQProducerTopicTx(String identity, AcknowledgeType type, Consumer<Session> consumer) {
        IbmMQFactory.produceQueue(identity, true, type, consumer);
    }
}
