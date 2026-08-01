package ir.moke.microfox.logger.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.logger.LoggerManager;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import ir.moke.microfox.utils.LogUtils;
import org.slf4j.LoggerFactory;

import java.util.logging.LogManager;

public class ConsoleAppender {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public static void addConsoleLogger(ConsoleGenericModel log, Encoder<ILoggingEvent> encoder) {
        addConsoleLogger(
                log.getAppenderName(),
                log.getPackageName(),
                log.getLevel(),
                encoder
        );
    }

    public static void addConsoleLogger(String name, String packageName, Level level,
                                        Encoder<ILoggingEvent> encoder) {

        Logger log = loggerContext.getLogger(packageName);
        LoggerManager.detachLoggerAppender(name,packageName);

        ch.qos.logback.core.ConsoleAppender<ILoggingEvent> consoleAppender = new ch.qos.logback.core.ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setName(name);
        consoleAppender.setImmediateFlush(true);


        encoder.setContext(loggerContext);
        if (!encoder.isStarted()) encoder.start();

        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        LogUtils.setFilter(level, consoleAppender);
        log.setAdditive(false);
        log.addAppender(consoleAppender);
    }

    private static ch.qos.logback.core.ConsoleAppender<ILoggingEvent> getConsoleAppender(String name, Encoder<ILoggingEvent> encoder) {
        ch.qos.logback.core.ConsoleAppender<ILoggingEvent> consoleAppender = new ch.qos.logback.core.ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setName(name);
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();
        return consoleAppender;
    }

}
