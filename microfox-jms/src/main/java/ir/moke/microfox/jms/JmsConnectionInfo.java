package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;

import java.util.concurrent.CountDownLatch;

public class JmsConnectionInfo {
    private ConnectionFactory connectionFactory;
    private JMSConsumer consumer;
    private JMSContext context;
    private String destination;
    private AckMode mode;
    private DestinationType type;
    private boolean connected;
    private int concurrency;
    private int maxConcurrency;
    private int keepAliveTimeout;
    private MessageListener listener;
    private CountDownLatch latch;

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

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public void setMaxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }

    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public MessageListener getListener() {
        return listener;
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
