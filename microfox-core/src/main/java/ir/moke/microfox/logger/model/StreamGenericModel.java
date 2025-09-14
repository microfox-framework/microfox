package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

import java.io.OutputStream;

public class StreamGenericModel extends GenericModel {
    private final OutputStream outputStream;

    public StreamGenericModel(String appenderName, String packageName, Level level, OutputStream outputStream) {
        super(appenderName, packageName, level);
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
