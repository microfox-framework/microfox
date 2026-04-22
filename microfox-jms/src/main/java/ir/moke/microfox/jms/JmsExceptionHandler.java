package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.JmsConnectionInfo;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsExceptionHandler implements ExceptionListener {
    private static final Logger logger = LoggerFactory.getLogger(JmsExceptionHandler.class);
    private final String identity;
    private final JmsProviderImpl provider = new JmsProviderImpl();

    public JmsExceptionHandler(String identity) {
        this.identity = identity;
    }

    @Override
    public void onException(JMSException exception) {
        logger.debug("Jms identity:{} Exception:{}", identity, exception.getErrorCode());
        JmsConnectionInfo info = JmsFactory.close(identity);

        if (info != null) {
            provider.consumeMessage(identity, info.getDestination(), info.getMode(), info.getType(), info.getListener(), info.getConnectionFactory());
            logger.info("Reconnected JMS consumer: {}", identity);
        }
    }
}
