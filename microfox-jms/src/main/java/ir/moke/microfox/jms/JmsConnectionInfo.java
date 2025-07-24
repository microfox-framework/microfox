package ir.moke.microfox.jms;

import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;

public class JmsConnectionInfo {
    private JMSConsumer consumer;
    private JMSContext context;
    private boolean connected;

    public JmsConnectionInfo(boolean connected) {
        this.connected = connected;
    }

    public JmsConnectionInfo(JMSContext context, JMSConsumer consumer, boolean connected) {
        this.consumer = consumer;
        this.context = context;
        this.connected = connected;
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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
