package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.utils.LogUtils;

public class SseGenericModel extends GenericModel {

    public SseGenericModel(String appenderName, String packageName, Level level, Encoder<ILoggingEvent> encoder) {
        super(appenderName, packageName, level, encoder);
    }

    public SseGenericModel(String appenderName, String packageName, Level level) {
        super(appenderName, packageName, level, LogUtils.getEncoder());
    }
}
