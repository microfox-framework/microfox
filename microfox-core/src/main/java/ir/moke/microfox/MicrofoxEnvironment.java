package ir.moke.microfox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static ir.moke.microfox.utils.TtyAsciiCodecs.*;

public class MicrofoxEnvironment {
    private static final Logger logger = LoggerFactory.getLogger(MicrofoxEnvironment.class);
    private static final Map<Object, Object> sortedMap = new TreeMap<>(loadEnvironments());

    private static void printEnvironments() {
        Set<Object> keys = sortedMap.keySet();
        for (Object o : keys) {
            String key = (String) o;
            String value = (String) sortedMap.get(key);
            if (key.startsWith("MICROFOX_")) {
                if (value.isEmpty()) {
                    logger.info("{}{}{}", BACKGROUND_YELLOW, key, RESET);
                } else {
                    if (key.endsWith("PASSWORD")) value = "********************************";
                    logger.info("{}{}{} {}", GREEN, key, RESET, value);
                }
            }
        }
    }

    private static void printLogo() {
        try (InputStream inputStream = MicrofoxEnvironment.class.getClassLoader().getResourceAsStream("logo")) {
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

    private static Properties loadEnvironments() {
        Properties properties = new Properties();
        try {
            Enumeration<URL> resources = MicrofoxEnvironment.class.getClassLoader().getResources("application.properties");
            List<URL> urls = Collections.list(resources).reversed(); // only for apply application config after accept all default values
            for (URL url : urls) {
                try (InputStream is = url.openStream()) {
                    properties.load(is);
                }
            }
            return properties;
        } catch (IOException e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
        return properties;
    }

    public static String getEnv(String key) {
        return (String) sortedMap.get(key);
    }
}
