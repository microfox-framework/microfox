package ir.moke.microfox.jms;

import jakarta.jms.JMSContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsUtils {
    private static final Logger logger = LoggerFactory.getLogger(JmsUtils.class);

    public static void contextClose(JMSContext context) {
        try {
            context.close();
        } catch (Exception e) {
            logger.warn("Error closing context", e);
        }
    }

    public static void contextStop(JMSContext context) {
        try {
            context.stop();
        } catch (Exception e) {
            logger.warn("Error stop context", e);
        }
    }

    public static void contextStart(JMSContext context) {
        try {
            context.start();
        } catch (Exception e) {
            logger.warn("Error start context", e);
        }
    }

    public static void contextRecover(JMSContext context) {
        try {
            context.recover();
        } catch (Exception e) {
            logger.warn("Error recover context", e);
        }
    }
}
