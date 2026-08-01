package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.utils.LogUtils;

public class ConsoleGenericModel extends GenericModel {
    public ConsoleGenericModel(String appenderName, String packageName, Level level, Encoder<ILoggingEvent> encoder, boolean isAsync) {
        super(appenderName, packageName, level, encoder, isAsync);
    }

    public ConsoleGenericModel(String appenderName, String packageName, Level level, boolean isAsync) {
        super(appenderName, packageName, level, LogUtils.getEncoder(), isAsync);
    }

    public ConsoleGenericModel(String appenderName, String packageName, Level level, Encoder<ILoggingEvent> encoder) {
        super(appenderName, packageName, level, encoder, false);
    }

    public ConsoleGenericModel(String appenderName, String packageName, Level level) {
        super(appenderName, packageName, level, LogUtils.getEncoder(), false);
    }
}
