package ir.moke.microfox.http;

import ir.moke.microfox.ApplicationEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class HttpContainerConfig {
    private static final Logger logger = LoggerFactory.getLogger(HttpContainerConfig.class);
    public static String MICROFOX_HTTP_HOST;
    public static String MICROFOX_HTTP_PORT;
    public static String MICROFOX_HTTP_BASE_API;
    public static String MICROFOX_OPEN_API_TITLE;
    public static String MICROFOX_OPEN_API_VERSION;
    public static String MICROFOX_OPEN_API_DESCRIPTION;
    public static String MICROFOX_RESOURCE_BUNDLE_NAME;

    static {
        try (InputStream is = ApplicationEnvironment.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            MICROFOX_HTTP_HOST = Optional.ofNullable(System.getenv("MICROFOX_HTTP_HOST")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_HOST"))).orElse("0.0.0.0");
            MICROFOX_HTTP_PORT = Optional.ofNullable(System.getenv("MICROFOX_HTTP_PORT")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_PORT"))).orElse("8080");
            MICROFOX_HTTP_BASE_API = Optional.ofNullable(System.getenv("MICROFOX_HTTP_BASE_API")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_HTTP_BASE_API"))).orElse("/");
            MICROFOX_OPEN_API_TITLE = Optional.ofNullable(System.getenv("MICROFOX_OPEN_API_TITLE")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_OPEN_API_TITLE"))).orElse("Microfox OpenAPI");
            MICROFOX_OPEN_API_VERSION = Optional.ofNullable(System.getenv("MICROFOX_OPEN_API_VERSION")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_OPEN_API_VERSION"))).orElse("0.2");
            MICROFOX_OPEN_API_DESCRIPTION = Optional.ofNullable(System.getenv("MICROFOX_OPEN_API_DESCRIPTION")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_OPEN_API_DESCRIPTION"))).orElse("MicroFox OpenAPI is a lightweight, modular Java microframework designed for building RestFul APIs with minimal configuration. It provides a streamlined approach to developing scalable and maintainable web services, featuring built-in support for routing, request handling, and OpenAPI documentation generation. Ideal for microservices and modern cloud-native applications, MicroFox emphasizes simplicity, extensibility, and developer productivity.");
            MICROFOX_RESOURCE_BUNDLE_NAME = Optional.ofNullable(System.getenv("MICROFOX_RESOURCE_BUNDLE_NAME")).or(() -> Optional.ofNullable(properties.getProperty("MICROFOX_RESOURCE_BUNDLE_NAME"))).orElse("MicroFoxValidation");
        } catch (Exception e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
    }
}
