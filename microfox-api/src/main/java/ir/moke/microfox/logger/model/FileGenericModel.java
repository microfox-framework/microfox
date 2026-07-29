package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.utils.LogUtils;

public class FileGenericModel extends GenericModel {
    private final String filePath;
    private final String fileNamePattern;
    private final String maxFileSize;
    private final int maxHistory;
    private final String totalSizeCap;

    public FileGenericModel(String appenderName, String packageName, Level level, String filePath, String fileNamePattern, Encoder<ILoggingEvent> encoder, String maxFileSize, int maxHistory, String totalSizeCap) {
        super(appenderName, packageName, level, encoder);
        this.filePath = filePath;
        this.fileNamePattern = fileNamePattern;
        this.maxFileSize = maxFileSize;
        this.maxHistory = maxHistory;
        this.totalSizeCap = totalSizeCap;
    }

    public FileGenericModel(String appenderName, String packageName, Level level, String filePath, String fileNamePattern, String maxFileSize, int maxHistory, String totalSizeCap) {
        super(appenderName, packageName, level, LogUtils.getEncoder());
        this.filePath = filePath;
        this.fileNamePattern = fileNamePattern;
        this.maxFileSize = maxFileSize;
        this.maxHistory = maxHistory;
        this.totalSizeCap = totalSizeCap;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public int getMaxHistory() {
        return maxHistory;
    }

    public String getTotalSizeCap() {
        return totalSizeCap;
    }
}
