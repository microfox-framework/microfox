package ir.moke.microfox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static ir.moke.microfox.utils.TtyAsciiCodecs.GREEN;
import static ir.moke.microfox.utils.TtyAsciiCodecs.RESET;

public class MicrofoxEnvironment {
    private static final Logger logger = LoggerFactory.getLogger(MicrofoxEnvironment.class);

    private static void printEnvironments() {
        Map<String, String> envMap = System.getenv();
        for (String key : envMap.keySet()) {
            if (key.startsWith("MICROFOX_")) {
                String value = System.getenv(key);
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
}
