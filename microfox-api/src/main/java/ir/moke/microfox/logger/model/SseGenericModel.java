package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.utils.LogUtils;

import java.util.function.BiConsumer;

public class SseGenericModel extends GenericModel {

    private final String identity;
    private final BiConsumer<String, SseObject> ssePublisher;

    public SseGenericModel(String identity, String appenderName, String packageName, Level level, Encoder<ILoggingEvent> encoder, BiConsumer<String, SseObject> ssePublisher) {
        super(appenderName, packageName, level, encoder);
        this.identity = identity;
        this.ssePublisher = ssePublisher;
    }

    public SseGenericModel(String identity, String appenderName, String packageName, Level level, BiConsumer<String, SseObject> ssePublisher) {
        super(appenderName, packageName, level, LogUtils.getEncoder());
        this.identity = identity;
        this.ssePublisher = ssePublisher;
    }

    public String getIdentity() {
        return identity;
    }

    public BiConsumer<String, SseObject> getSsePublisher() {
        return ssePublisher;
    }
}
