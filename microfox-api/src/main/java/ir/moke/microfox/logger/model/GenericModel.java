package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;

import java.util.Objects;

public abstract class GenericModel implements LogModel {
    private final String appenderName;
    private final String packageName;
    private final Level level;
    private final Encoder<ILoggingEvent> encoder;
    private final boolean async;

    GenericModel(String appenderName, String packageName, Level level, Encoder<ILoggingEvent> encoder, boolean async) {
        this.appenderName = appenderName;
        this.packageName = packageName;
        this.level = level;
        this.encoder = encoder;
        this.async = async;
    }

    public String getPackageName() {
        return packageName;
    }

    public Level getLevel() {
        return level;
    }

    public String getAppenderName() {
        return appenderName;
    }

    @Override
    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public boolean isAsync() {
        return async;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GenericModel genericModel = (GenericModel) o;
        return Objects.equals(appenderName, genericModel.appenderName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(appenderName);
    }
}
