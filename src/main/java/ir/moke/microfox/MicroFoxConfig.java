package ir.moke.microfox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class MicroFoxConfig {
    private static final Logger logger = LoggerFactory.getLogger(MicroFoxConfig.class);
    public static String MICROFOX_HTTP_HOST;
    public static String MICROFOX_HTTP_PORT;
    public static String MICROFOX_HTTP_BASE_CONTEXT;
    public static String MICROFOX_OPEN_API_TITLE;
    public static String MICROFOX_OPEN_API_VERSION;
    public static String MICROFOX_OPEN_API_DESCRIPTION;

    static {
        try (InputStream is = MicroFoxConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            MICROFOX_HTTP_HOST = Optional.ofNullable(System.getenv("MICROFOX_HTTP_HOST")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_HOST"))).orElse("0.0.0.0");
            MICROFOX_HTTP_PORT = Optional.ofNullable(System.getenv("MICROFOX_HTTP_PORT")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_PORT"))).orElse("8080");
            MICROFOX_HTTP_BASE_CONTEXT = Optional.ofNullable(System.getenv("MICROFOX_HTTP_BASE_CONTEXT")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_BASE_CONTEXT"))).orElse("/");
            MICROFOX_OPEN_API_TITLE = Optional.ofNullable(System.getenv("MICROFOX_OPEN_API_TITLE")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_OPEN_API_TITLE"))).orElse("Microfox OpenAPI");
            MICROFOX_OPEN_API_VERSION = Optional.ofNullable(System.getenv("MICROFOX_OPEN_API_VERSION")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_OPEN_API_VERSION"))).orElse("0.2");
            MICROFOX_OPEN_API_DESCRIPTION = Optional.ofNullable(System.getenv("MICROFOX_OPEN_API_DESCRIPTION")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_OPEN_API_DESCRIPTION"))).orElse("MicroFox OpenAPI is a lightweight, modular Java microframework designed for building RestFul APIs with minimal configuration. It provides a streamlined approach to developing scalable and maintainable web services, featuring built-in support for routing, request handling, and OpenAPI documentation generation. Ideal for microservices and modern cloud-native applications, MicroFox emphasizes simplicity, extensibility, and developer productivity.");
        } catch (Exception e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
    }

    private static void printEnvironments() {
        logger.info("MICROFOX_HTTP_HOST {}", MICROFOX_HTTP_HOST);
        logger.info("MICROFOX_HTTP_PORT {}", MICROFOX_HTTP_PORT);
        logger.info("MICROFOX_HTTP_BASE_CONTEXT {}", MICROFOX_HTTP_BASE_CONTEXT);
        logger.info("MICROFOX_OPEN_API_TITLE {}", MICROFOX_OPEN_API_TITLE);
        logger.info("MICROFOX_OPEN_API_VERSION {}", MICROFOX_OPEN_API_DESCRIPTION);
    }

    private static void printLogo() {
        try (InputStream inputStream = MicroFoxConfig.class.getClassLoader().getResourceAsStream("logo")) {
            if (inputStream != null) {
                System.out.println(new String(inputStream.readAllBytes()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void introduce() {
        printLogo();
        printEnvironments();
    }
}
