package ir.moke.microfox.logger.appender;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.logger.LoggerManager;
import ir.moke.microfox.logger.model.SseGenericModel;
import ir.moke.microfox.utils.LogUtils;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public class SseAppender extends AppenderBase<ILoggingEvent> {
    private final String identity;
    private final BiConsumer<String, SseObject> ssePublisher;
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private final Encoder<ILoggingEvent> encoder;

    public static void addSseLogger(SseGenericModel model) {
        Logger logger = loggerContext.getLogger(model.getPackageName());
        SseAppender appender = new SseAppender(model.getIdentity(), model.getEncoder(), model.getSsePublisher());
        LoggerManager.detachLoggerAppender(model.getAppenderName(), model.getPackageName());
        appender.setContext(loggerContext);
        appender.setName("SSE-" + model.getIdentity());
        LogUtils.setFilter(model.getLevel(), appender);
        appender.start();
        logger.addAppender(appender);
        logger.setAdditive(true);
    }

    private SseAppender(String identity, Encoder<ILoggingEvent> encoder, BiConsumer<String, SseObject> ssePublisher) {
        this.identity = identity;
        this.ssePublisher = ssePublisher;
        this.encoder = encoder;
    }

    @Override
    protected void append(ILoggingEvent event) {
        byte[] bytes = encoder.encode(event);
        String formattedMessage = new String(bytes, StandardCharsets.UTF_8);
        SseObject sseEvent = new SseObject(formattedMessage);
        ssePublisher.accept(identity, sseEvent);
    }
}
