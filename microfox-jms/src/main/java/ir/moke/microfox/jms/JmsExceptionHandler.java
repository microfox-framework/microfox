package ir.moke.microfox.jms;

import ir.moke.microfox.api.jms.JmsProvider;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JmsExceptionHandler implements ExceptionListener {
    private static final Logger logger = LoggerFactory.getLogger(JmsExceptionHandler.class);
    private final String identity;

    public JmsExceptionHandler(String identity) {
        this.identity = identity;
    }

    @Override
    public void onException(JMSException exception) {
        logger.debug("Jms identity:{} Exception:{}", identity, exception.getErrorCode());
        JmsConnectionInfo info = JmsFactory.closeContext(identity);

        if (info != null) {
            // Retry after 3 seconds
            try (ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()) {
                scheduledExecutorService.schedule(() -> {
                    try {
                        JmsProviderImpl provider = new JmsProviderImpl();
                        provider.consumeMessage(identity, info.getDestination(), info.getMode(), info.getType(), info.getListener(),info.getConnectionFactory());
                        logger.info("Reconnected JMS consumer: {}", identity);
                    } catch (Exception ex) {
                        logger.error("Failed to reconnect JMS consumer for {}: {}", identity, ex.getMessage());
                    }
                }, 5, TimeUnit.SECONDS);
            }
        }
    }
}
