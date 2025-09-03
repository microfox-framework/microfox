package ir.moke.microfox.logger.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ir.moke.microfox.logger.model.FileLog;
import ir.moke.microfox.utils.LogUtils;
import org.slf4j.LoggerFactory;

public class FileAppender {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public static void addFileLogger(FileLog log) {
        addFileLogger(
                log.getAppenderName(),
                log.getPackageName(),
                log.getLevel(),
                log.getFilePath(),
                log.getPattern(),
                log.getFileNamePattern(),
                log.getMaxFileSize(),
                log.getTotalSizeCap(),
                log.getMaxHistory()
        );
    }

    public static void addFileLogger(String name, String packageName, Level level, String logPath, String pattern, String fileArchivePattern, String maxFileSize, String totalSize, int maxHistory) {
        RollingFileAppender<ILoggingEvent> fileAppender = getFileAppender(name, logPath, pattern, fileArchivePattern, maxFileSize, totalSize, maxHistory);
        LogUtils.setFilter(level, fileAppender);
        Logger logger = loggerContext.getLogger(packageName);
        logger.setAdditive(false);
        logger.addAppender(fileAppender);
    }

    private static RollingFileAppender<ILoggingEvent> getFileAppender(String name, String logPath, String pattern, String fileArchivePattern, String maxFileSize, String totalSize, int maxHistory) {
        PatternLayout patternLayout = LogUtils.getBasicPatternLayout(pattern);
        LayoutWrappingEncoder<ILoggingEvent> encoder = LogUtils.getEncoder(patternLayout);

        RollingFileAppender<ILoggingEvent> logFileAppender = new RollingFileAppender<>();
        logFileAppender.setContext(loggerContext);
        logFileAppender.setName(name);
        logFileAppender.setEncoder(encoder);
        logFileAppender.setAppend(true);
        logFileAppender.setFile(logPath);

        SizeAndTimeBasedRollingPolicy<ILoggingEvent> logFilePolicy = new SizeAndTimeBasedRollingPolicy<>();
        logFilePolicy.setContext(loggerContext);
        logFilePolicy.setParent(logFileAppender);
        logFilePolicy.setFileNamePattern(fileArchivePattern);
        logFilePolicy.setMaxHistory(maxHistory);
        logFilePolicy.setTotalSizeCap(FileSize.valueOf(totalSize));
        logFilePolicy.setMaxFileSize(FileSize.valueOf(maxFileSize));
        logFilePolicy.start();

        logFileAppender.setRollingPolicy(logFilePolicy);
        logFileAppender.start();
        return logFileAppender;
    }
}
