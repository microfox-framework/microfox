package ir.moke.microfox.logger.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ir.moke.microfox.api.http.sse.SseObject;

import java.util.function.BiConsumer;

public class SseAppender extends AppenderBase<ILoggingEvent> {
    private final String identity;
    private final BiConsumer<String, SseObject> ssePublisher;

    public SseAppender(String identity, BiConsumer<String, SseObject> ssePublisher) {
        this.identity = identity;
        this.ssePublisher = ssePublisher;
    }

    @Override
    protected void append(ILoggingEvent event) {
        String formattedMessage = event.getFormattedMessage();
        SseObject sseEvent = new SseObject(formattedMessage);
        ssePublisher.accept(identity, sseEvent);
    }
}
