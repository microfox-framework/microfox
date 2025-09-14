package ir.moke.test;

import ch.qos.logback.classic.Level;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.logger.model.ConsoleLogModel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void checkLog() {
        MicroFox.logger(new ConsoleLogModel("CL", "ir.moke.test", Level.TRACE));
        logger.info("Info Log");
        logger.debug("Debug Log");
        logger.error("Error Log");
    }
}
