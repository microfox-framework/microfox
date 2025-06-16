package ir.moke.microfox.jms;

import ir.moke.microfox.api.jpa.DestinationType;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsExceptionHandler implements ExceptionListener {
    private static final Logger logger = LoggerFactory.getLogger(JmsExceptionHandler.class);
    private final String identity;
    private final String destinationName;
    private final DestinationType destinationType;
    private final int acknowledgeMode;
    private final MessageListener listener;

    public JmsExceptionHandler(String identity, DestinationType destinationType, String destinationName, int acknowledgeMode, MessageListener listener) {
        this.identity = identity;
        this.destinationType = destinationType;
        this.destinationName = destinationName;
        this.acknowledgeMode = acknowledgeMode;
        this.listener = listener;
    }

    @Override
    public void onException(JMSException exception) {
        logger.debug("Jms Exception: {}", exception.getErrorCode());
        JmsFactory.closeContext(identity);
        switch (destinationType) {
            case QUEUE -> new JmsProviderImpl().consumeQueue(identity, destinationName, acknowledgeMode, listener);
            case TOPIC -> new JmsProviderImpl().consumeTopic(identity, destinationName, acknowledgeMode, listener);
        }
    }
}
