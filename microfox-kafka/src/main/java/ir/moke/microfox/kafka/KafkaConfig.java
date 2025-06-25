package ir.moke.microfox.kafka;

import ir.moke.microfox.MicrofoxEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class KafkaConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
    public static String MICROFOX_KAFKA_POOL_TIMEOUT;
    public static String MICROFOX_KAFKA_IDLE_BETWEEN_POOL;

    static {
        try (InputStream is = MicrofoxEnvironment.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            MICROFOX_KAFKA_POOL_TIMEOUT = Optional.ofNullable(System.getenv("MICROFOX_KAFKA_POOL_TIMEOUT")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_KAFKA_POOL_TIMEOUT"))).orElse("50000");
            MICROFOX_KAFKA_IDLE_BETWEEN_POOL = Optional.ofNullable(System.getenv("MICROFOX_KAFKA_IDLE_BETWEEN_POOL")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_KAFKA_IDLE_BETWEEN_POOL"))).orElse("3000");
        } catch (Exception e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
    }
}
