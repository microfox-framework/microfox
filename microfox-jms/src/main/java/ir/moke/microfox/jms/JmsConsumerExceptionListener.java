package ir.moke.microfox.jms;

import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsConsumerExceptionListener implements ExceptionListener {
    private static final Logger logger = LoggerFactory.getLogger(JmsConsumerExceptionListener.class);

    @Override
    public void onException(JMSException exception) {
        logger.error("Jms exception - {} {}", exception.getErrorCode(), exception.getMessage());
    }
}
