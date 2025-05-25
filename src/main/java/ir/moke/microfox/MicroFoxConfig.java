package ir.moke.microfox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class MicroFoxConfig {
    private static final Logger logger = LoggerFactory.getLogger(MicroFoxConfig.class);
    public static String MICROFOX_HTTP_HOST;
    public static String MICROFOX_HTTP_PORT;
    public static String MICROFOX_HTTP_BASE_CONTEXT;

    static {
        try (InputStream is = MicroFoxConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            MICROFOX_HTTP_HOST = Optional.ofNullable(System.getenv("MICROFOX_HTTP_HOST")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_HOST"))).orElse("0.0.0.0");
            MICROFOX_HTTP_PORT = Optional.ofNullable(System.getenv("MICROFOX_HTTP_PORT")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_PORT"))).orElse("8080");
            MICROFOX_HTTP_BASE_CONTEXT = Optional.ofNullable(System.getenv("MICROFOX_HTTP_BASE_CONTEXT")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_BASE_CONTEXT"))).orElse("/");
        } catch (Exception e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
    }

    public static void print() {
        logger.info("MICROFOX_HTTP_HOST {}", MICROFOX_HTTP_HOST);
        logger.info("MICROFOX_HTTP_PORT {}", MICROFOX_HTTP_PORT);
        logger.info("MICROFOX_HTTP_BASE_CONTEXT {}", MICROFOX_HTTP_BASE_CONTEXT);
    }
}
