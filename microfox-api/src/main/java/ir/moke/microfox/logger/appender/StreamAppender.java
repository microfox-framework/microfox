package ir.moke.microfox.logger.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.logger.LoggerManager;
import ir.moke.microfox.logger.model.StreamGenericModel;
import ir.moke.microfox.utils.LogUtils;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class StreamAppender {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public static void addOutputStreamLogger(StreamGenericModel log) {
        addOutputStreamLogger(
                log.getAppenderName(),
                log.getPackageName(),
                log.getLevel(),
                log.getOutputStream(),
                log.getEncoder(),
                log.isAsync()
        );
    }

    public static void addOutputStreamLogger(String name, String packageName, Level level, OutputStream outputStream, Encoder<ILoggingEvent> encoder, boolean isAsync) {
        OutputStreamAppender<ILoggingEvent> outputStreamAppender = getOutputStreamAppender(name, outputStream, encoder);
        Logger log = loggerContext.getLogger(packageName);
        LoggerManager.detachLoggerAppender(name, packageName);

        LogUtils.setFilter(level, outputStreamAppender);

        log.setAdditive(false);
        log.addAppender(isAsync ? LogUtils.getAsyncAppender("async-" + name, outputStreamAppender) : outputStreamAppender);
    }

    private static OutputStreamAppender<ILoggingEvent> getOutputStreamAppender(String name, OutputStream outputStream, Encoder<ILoggingEvent> encoder) {
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
