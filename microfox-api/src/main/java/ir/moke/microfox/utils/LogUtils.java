package ir.moke.microfox.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ir.moke.microfox.logger.LogHighlighter;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class LogUtils {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    public static final String DEFAULT_CONSOLE_PATTERN = "%d{yyyy-MMM-dd HH:mm:ss} [%t] [%highlighter(%-5level)] %logger{36} [%M:%L] - %msg%n";
    public static final String DEFAULT_SYSLOG_PATTERN = "[%t] [%-5level] %logger{36} [%M:%L] - %msg%n";

    public static void setFilter(Level level, Appender<ILoggingEvent> appender) {
        appender.clearAllFilters();
        ThresholdFilter levelFilter = new ThresholdFilter();
        levelFilter.setLevel(level.levelStr);
        appender.addFilter(levelFilter);
        levelFilter.start();
    }

    public static LayoutWrappingEncoder<ILoggingEvent> getEncoder() {
        return getEncoder(getBasicPatternLayout(null));
    }

    public static LayoutWrappingEncoder<ILoggingEvent> getEncoder(Layout<ILoggingEvent> layout) {
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(loggerContext);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.setLayout(layout);
        encoder.setImmediateFlush(true);

        layout.start();
        return encoder;
    }

    public static PatternLayout getBasicPatternLayout(String pattern) {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(loggerContext);
        patternLayout.setPattern(pattern != null ? pattern : DEFAULT_CONSOLE_PATTERN);
        patternLayout.getInstanceConverterMap().put("highlighter", LogHighlighter::new);
        patternLayout.start();
        return patternLayout;
    }
}
