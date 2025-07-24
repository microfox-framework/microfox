package ir.moke.microfox.jms;

import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsExceptionHandler implements ExceptionListener {
    private static final Logger logger = LoggerFactory.getLogger(JmsExceptionHandler.class);
    private final String identity;

    public JmsExceptionHandler(String identity) {
        this.identity = identity;
    }

    @Override
    public void onException(JMSException exception) {
        logger.debug("Jms identity:{} Exception:{}", identity, exception.getErrorCode());
        JmsFactory.closeContext(identity);
    }
}
