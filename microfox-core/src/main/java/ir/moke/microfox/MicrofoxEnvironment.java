package ir.moke.microfox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import static ir.moke.microfox.utils.TtyAsciiCodecs.GREEN;
import static ir.moke.microfox.utils.TtyAsciiCodecs.RESET;

public class MicrofoxEnvironment {
    private static final Logger logger = LoggerFactory.getLogger(MicrofoxEnvironment.class);
    private static final Properties properties = loadEnvironments();

    private static void printEnvironments() {
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = properties.getProperty(key);
            if (key.startsWith("MICROFOX_")) {
                if (key.endsWith("PASSWORD")) value = "********************************";
                logger.info("{}{}{} {}", GREEN, key, RESET, value);
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
        try (InputStream is = MicrofoxEnvironment.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) properties.load(is);
            properties.putAll(System.getenv());
        } catch (Exception e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
        return properties;
    }

    public static String getEnv(String key) {
        return properties.getProperty(key);
    }
}
