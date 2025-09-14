package ir.moke.microfox.logger.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import ir.moke.microfox.utils.LogUtils;
import org.slf4j.LoggerFactory;

public class ConsoleAppender {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public static void addConsoleLogger(ConsoleGenericModel log) {
        addConsoleLogger(
                log.getAppenderName(),
                log.getPackageName(),
                log.getLevel(),
                log.getPattern()
        );
    }

    public static void addConsoleLogger(String name, String packageName, Level level, String pattern) {
        ch.qos.logback.core.ConsoleAppender<ILoggingEvent> consoleAppender = getConsoleAppender(name, pattern);
        Logger log = loggerContext.getLogger(packageName);
        if (level == Level.TRACE) {
            log.setLevel(level);
        } else {
            LogUtils.setFilter(level, consoleAppender);
        }
        log.setAdditive(false);
        log.addAppender(consoleAppender);
    }

    private static ch.qos.logback.core.ConsoleAppender<ILoggingEvent> getConsoleAppender(String name, String pattern) {
        PatternLayout patternLayout = LogUtils.getBasicPatternLayout(pattern);
        LayoutWrappingEncoder<ILoggingEvent> encoder = LogUtils.getEncoder(patternLayout);
        ch.qos.logback.core.ConsoleAppender<ILoggingEvent> consoleAppender = new ch.qos.logback.core.ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setName(name);
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();
        return consoleAppender;
    }

}
