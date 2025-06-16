package ir.moke.microfox.jms;

import ir.moke.microfox.MicrofoxEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class JmsConfig {
    private static final Logger logger = LoggerFactory.getLogger(JmsConfig.class);
    public static String MICROFOX_JMS_RETRY_TIMEOUT;

    static {
        try (InputStream is = MicrofoxEnvironment.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            MICROFOX_JMS_RETRY_TIMEOUT = Optional.ofNullable(System.getenv("MICROFOX_JMS_RETRY_TIMEOUT")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_JMS_RETRY_TIMEOUT"))).orElse("5");
        } catch (Exception e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
    }
}
