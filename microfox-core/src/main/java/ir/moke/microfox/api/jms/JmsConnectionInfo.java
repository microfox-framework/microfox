package ir.moke.microfox.api.jms;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class JmsConnectionInfo {
    private String identity;
    private ConnectionFactory connectionFactory;
    private JMSConsumer consumer;
    private JMSContext context;
    private String destination;
    private AckMode mode;
    private DestinationType type;
    private boolean connected;
    private int concurrency;
    private MessageListener listener;
    private ExecutorService executorService;

    public JmsConnectionInfo() {
        this.connected = false;
    }

    public JmsConnectionInfo(ConnectionFactory connectionFactory, JMSContext context, JMSConsumer consumer, String destination, AckMode mode, DestinationType type, MessageListener listener) {
        this.connectionFactory = connectionFactory;
        this.consumer = consumer;
        this.context = context;
        this.destination = destination;
        this.mode = mode;
        this.type = type;
        this.listener = listener;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public JMSConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(JMSConsumer consumer) {
        this.consumer = consumer;
    }

    public JMSContext getContext() {
        return context;
    }

    public void setContext(JMSContext context) {
        this.context = context;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public AckMode getMode() {
        return mode;
    }

    public void setMode(AckMode mode) {
        this.mode = mode;
    }

    public DestinationType getType() {
        return type;
    }

    public void setType(DestinationType type) {
        this.type = type;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public MessageListener getListener() {
        return listener;
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JmsConnectionInfo that = (JmsConnectionInfo) o;
        return Objects.equals(identity, that.identity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identity);
    }
}
