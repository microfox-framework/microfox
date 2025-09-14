package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

import java.io.OutputStream;

public class StreamLogModel extends LogModel {
    private final OutputStream outputStream;

    public StreamLogModel(String appenderName, String packageName, Level level, OutputStream outputStream) {
        super(appenderName, packageName, level);
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
