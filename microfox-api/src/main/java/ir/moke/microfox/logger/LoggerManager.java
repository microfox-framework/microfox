package ir.moke.microfox.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ir.moke.microfox.logger.appender.*;
import ir.moke.microfox.logger.model.*;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoggerManager {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    static {
        loggerContext.reset();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.TRACE);
    }

    public static List<Logger> list() {
        return loggerContext.getLoggerList();
    }

    public static void registerLog(LogModel log) {
        switch (log) {
            case SysGenericModel sysLog -> SyslogAppender.addSyslogLogger(sysLog);
            case FileGenericModel fileLog -> FileAppender.addFileLogger(fileLog);
            case StreamGenericModel streamLog -> StreamAppender.addOutputStreamLogger(streamLog);
            case ConsoleGenericModel consoleLog -> ConsoleAppender.addConsoleLogger(consoleLog, log.getEncoder());
            case SseGenericModel sseLog -> SseAppender.addSseLogger(sseLog);
            default -> throw new IllegalStateException("Unexpected value: " + log);
        }
    }

    public static void detachLoggerAppender(String name, String packageName) {
        Logger logger = loggerContext.getLogger(packageName);
        if (logger != null) {
            Appender<ILoggingEvent> asyncAppender = logger.getAppender("async-" + name);
            if (asyncAppender != null) {
                asyncAppender.stop();
                logger.detachAppender(asyncAppender);
            }

            Appender<ILoggingEvent> appender = logger.getAppender(name);
            if (appender != null) {
                appender.stop();
                logger.detachAppender(appender);
            }
        }
    }
}
