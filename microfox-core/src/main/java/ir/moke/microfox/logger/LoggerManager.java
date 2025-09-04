package ir.moke.microfox.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.CoreConstants;
import ir.moke.microfox.logger.appender.ConsoleAppender;
import ir.moke.microfox.logger.appender.FileAppender;
import ir.moke.microfox.logger.appender.StreamAppender;
import ir.moke.microfox.logger.appender.SyslogAppender;
import ir.moke.microfox.logger.model.*;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoggerManager {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    static {
        loggerContext.reset();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.TRACE);

        loggerContext.putObject(CoreConstants.PATTERN_RULE_REGISTRY, Map.of("highlighter", LogHighlighter.class.getCanonicalName()));
    }

    public static void registerLog(LogInfo log) {
        switch (log) {
            case SysLogInfo sysLog -> SyslogAppender.addSyslogLogger(sysLog);
            case FileLogInfo fileLog -> FileAppender.addFileLogger(fileLog);
            case StreamLogInfo streamLog -> StreamAppender.addOutputStreamLogger(streamLog);
            case ConsoleLogInfo consoleLog -> ConsoleAppender.addConsoleLogger(consoleLog);
            default -> throw new UnsupportedOperationException("Log type not supported yet !");
        }
    }

    public static void detachLoggerAppender(String appenderName, String packageName) {
        Logger logger = loggerContext.getLogger(packageName);
        if (logger != null) {
            Appender<ILoggingEvent> appender = logger.getAppender(appenderName);
            if (appender != null) {
                appender.stop();
                logger.detachAppender(appender);
            }
        }
    }
}
