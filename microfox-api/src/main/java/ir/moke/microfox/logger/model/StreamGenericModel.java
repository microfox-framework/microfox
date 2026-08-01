package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.utils.LogUtils;

import java.io.OutputStream;

public class StreamGenericModel extends GenericModel {
    private final OutputStream outputStream;

    public StreamGenericModel(String appenderName, String packageName, Level level, OutputStream outputStream, Encoder<ILoggingEvent> encoder, boolean isAsync) {
        super(appenderName, packageName, level, encoder, isAsync);
        this.outputStream = outputStream;
    }

    public StreamGenericModel(String appenderName, String packageName, Level level, OutputStream outputStream, boolean isAsync) {
        super(appenderName, packageName, level, LogUtils.getEncoder(), isAsync);
        this.outputStream = outputStream;
    }

    public StreamGenericModel(String appenderName, String packageName, Level level, OutputStream outputStream, Encoder<ILoggingEvent> encoder) {
        super(appenderName, packageName, level, encoder, false);
        this.outputStream = outputStream;
    }

    public StreamGenericModel(String appenderName, String packageName, Level level, OutputStream outputStream) {
        super(appenderName, packageName, level, LogUtils.getEncoder(), false);
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
