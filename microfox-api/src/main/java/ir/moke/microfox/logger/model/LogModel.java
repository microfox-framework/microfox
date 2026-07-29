package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;

public interface LogModel {
    String getPackageName();

    Level getLevel();

    String getAppenderName();

    Encoder<ILoggingEvent> getEncoder();
}
