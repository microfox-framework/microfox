package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

public class FileLogInfo extends LogInfo {
    private String pattern;
    private final String filePath;
    private final String fileNamePattern;
    private final String maxFileSize;
    private final int maxHistory;
    private final String totalSizeCap;

    public FileLogInfo(String appenderName, String packageName, Level level, String filePath, String fileNamePattern, String maxFileSize, int maxHistory, String totalSizeCap) {
        super(appenderName, packageName, level);
        this.filePath = filePath;
        this.fileNamePattern = fileNamePattern;
        this.maxFileSize = maxFileSize;
        this.maxHistory = maxHistory;
        this.totalSizeCap = totalSizeCap;
    }

    public FileLogInfo(String name, String packageName, Level level, String pattern, String filePath, String fileNamePattern, String maxFileSize, int maxHistory, String totalSizeCap) {
        super(name, packageName, level);
        this.pattern = pattern;
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

    public String getPattern() {
        return pattern;
    }
}
