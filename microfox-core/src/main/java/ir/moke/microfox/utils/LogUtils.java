package ir.moke.microfox.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.JsonFormatter;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ir.moke.kafir.utils.JsonUtils;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class LogUtils {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static final String DEFAULT_CONSOLE_PATTERN = "%d{yyyy-MMM-dd HH:mm:ss} [%t] [%highlighter(%-5level)] %logger{36} [%M:%L] - %msg%n";
    public static final String DEFAULT_SYSLOG_PATTERN = "[%t] [%-5level] %logger{36} [%M:%L] - %msg%n";

    public static void setFilter(Level level, Appender<ILoggingEvent> appender) {
        appender.clearAllFilters();
        ThresholdFilter levelFilter = new ThresholdFilter();
//        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(level.levelStr);
        appender.addFilter(levelFilter);
        levelFilter.start();
    }

    public static LayoutWrappingEncoder<ILoggingEvent> getEncoder(Layout<ILoggingEvent> pattern) {
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(loggerContext);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.setLayout(pattern);
        return encoder;
    }

    public static PatternLayout getBasicPatternLayout(String pattern) {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(loggerContext);
        patternLayout.setPattern(pattern != null ? pattern : DEFAULT_CONSOLE_PATTERN);
        patternLayout.start();
        return patternLayout;
    }

    public static Layout<ILoggingEvent> getJsonPatternLayout() {
        JsonFormatter formatter = JsonUtils::toJson;
        JsonLayout layout = new JsonLayout();
        layout.setContext(loggerContext);
        layout.setJsonFormatter(formatter);
        layout.setAppendLineSeparator(true);
        layout.setIncludeException(true);
        layout.setIncludeLoggerName(true);
        layout.setIncludeContextName(false);
        layout.start();
        return layout;
    }
}
