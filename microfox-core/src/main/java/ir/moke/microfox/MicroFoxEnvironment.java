package ir.moke.microfox;

import ir.moke.utils.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static ir.moke.utils.TtyAsciiCodecs.*;

public class MicroFoxEnvironment {
    private static final Logger logger = LoggerFactory.getLogger(MicroFoxEnvironment.class);
    private static final Map<Object, Object> sortedMap = new TreeMap<>(loadEnvironments());

    private static void printEnvironments() {
        Set<Object> keys = sortedMap.keySet();
        for (Object o : keys) {
            String key = (String) o;
            String value = (String) sortedMap.get(key);
            if (key.startsWith("MICROFOX".toLowerCase())) {
                if (value == null || value.isEmpty()) {
                    logger.info("{}{}{}", BACKGROUND_YELLOW, key, RESET);
                } else {
                    if (key.endsWith("PASSWORD".toLowerCase())) value = "********************************";
                    logger.info("{}{}{} {}", GREEN, key, RESET, value);
                }
            }
        }
    }

    private static void printLogo() {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("logo")) {
            if (inputStream != null) {
                byte[] bytes = inputStream.readAllBytes();
                System.out.write(bytes);
                System.out.flush();
            }
        } catch (Exception e) {
            logger.error("Unknown error", e);
        }
    }

    public static void introduce() {
        printLogo();
        printEnvironments();
    }

    private static Properties loadEnvironments() {
        Properties properties = new Properties();
        try {
            // Load and flatten application.properties (if exists)
            properties.putAll(loadPropertiesFile());

            // Load and flatten application.yaml (if exists)
            properties.putAll(loadYamlResources());

            // Merge environment variables
            properties.putAll(System.getenv());
            return properties;
        } catch (IOException e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
        return properties;
    }

    private static Properties loadPropertiesFile() throws IOException {
        Properties properties = new Properties();
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("application.properties");
        List<URL> urls = Collections.list(resources).reversed(); // only for apply application config after accept all default values
        for (URL url : urls) {
            Files.readAllLines(Path.of(url.getFile())).stream()
                    .filter(item -> !item.startsWith("#"))
                    .filter(item -> !item.isEmpty())
                    .map(item -> item.split("=", 2))
                    .forEach(item -> properties.setProperty(normalizeKey(item[0].toLowerCase()), item[1]));
        }
        return properties;
    }

    public static String getEnv(String key) {
        return (String) sortedMap.get(normalizeKey(key));
    }

    private static Properties loadYamlResources() {
        Properties props = new Properties();
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("application.yaml");
            if (resources == null) return props;
            List<URL> urls = Collections.list(resources).reversed(); // only for apply application config after accept all default values
            for (URL url : urls) {
                try (InputStream is = url.openStream()) {
                    props.putAll(YamlUtils.loadAsProperties(is));
                }
            }
        } catch (IOException e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
        return props;
    }

    public static String normalizeKey(String key) {
        // make dash/underscore/dot interchangeable → all to dot
        return key.replace("-", ".").replace("_", ".").toLowerCase();
    }
}
