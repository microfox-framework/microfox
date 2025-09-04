package ir.moke.microfox.logger.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ir.moke.microfox.logger.model.StreamLogInfo;
import ir.moke.microfox.utils.LogUtils;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class StreamAppender {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public static void addOutputStreamLogger(StreamLogInfo log) {
        addOutputStreamLogger(
                log.getAppenderName(),
                log.getPackageName(),
                log.getLevel(),
                log.getOutputStream()
        );
    }

    public static void addOutputStreamLogger(String name, String packageName, Level level, OutputStream outputStream) {
        OutputStreamAppender<ILoggingEvent> outputStreamAppender = getOutputStreamAppender(name, outputStream);

        LogUtils.setFilter(level, outputStreamAppender);

        Logger log = loggerContext.getLogger(packageName);
        log.setAdditive(false);
        log.addAppender(outputStreamAppender);
    }

    private static OutputStreamAppender<ILoggingEvent> getOutputStreamAppender(String name, OutputStream outputStream) {
        Layout<ILoggingEvent> layout = LogUtils.getJsonPatternLayout();
        LayoutWrappingEncoder<ILoggingEvent> encoder = LogUtils.getEncoder(layout);
        OutputStreamAppender<ILoggingEvent> outputStreamAppender = new OutputStreamAppender<>();
        outputStreamAppender.setContext(loggerContext);
        outputStreamAppender.setName(name);
        outputStreamAppender.setEncoder(encoder);
        outputStreamAppender.setOutputStream(outputStream);
        outputStreamAppender.setImmediateFlush(true);
        outputStreamAppender.start();
        return outputStreamAppender;
    }
}
