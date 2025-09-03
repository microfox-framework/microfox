package ir.moke.microfox.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.CoreConstants;
import ir.moke.microfox.logger.model.BaseLog;
import ir.moke.microfox.logger.appender.ConsoleAppender;
import ir.moke.microfox.logger.appender.FileAppender;
import ir.moke.microfox.logger.appender.StreamAppender;
import ir.moke.microfox.logger.appender.SyslogAppender;
import ir.moke.microfox.logger.model.ConsoleLog;
import ir.moke.microfox.logger.model.FileLog;
import ir.moke.microfox.logger.model.StreamLog;
import ir.moke.microfox.logger.model.SysLog;
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

    public static void registerLog(BaseLog log) {
        switch (log) {
            case SysLog sysLog -> SyslogAppender.addSyslogLogger(sysLog);
            case FileLog fileLog -> FileAppender.addFileLogger(fileLog);
            case StreamLog streamLog -> StreamAppender.addOutputStreamLogger(streamLog);
            case ConsoleLog consoleLog -> ConsoleAppender.addConsoleLogger(consoleLog);
            default -> throw new UnsupportedOperationException("Log type not supported yet !");
        }
    }

    public static void detachLoggerAppender(String name, String packageName) {
        Logger logger = loggerContext.getLogger(packageName);
        if (logger != null) {
            Appender<ILoggingEvent> appender = logger.getAppender(name);
            if (appender != null) {
                appender.stop();
                logger.detachAppender(appender);
            }
        }
    }

    public static void init() {
        ConsoleLog log = new ConsoleLog("microfox-console-log", "ir.moke.microfox", Level.DEBUG);
        LoggerManager.registerLog(log);
    }
}
