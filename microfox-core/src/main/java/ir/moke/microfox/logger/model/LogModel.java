package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

import java.util.Objects;

public class LogModel {
    private final String appenderName;
    private final String packageName;
    private final Level level;

    LogModel(String appenderName, String packageName, Level level) {
        this.appenderName = appenderName;
        this.packageName = packageName;
        this.level = level;
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LogModel logModel = (LogModel) o;
        return Objects.equals(appenderName, logModel.appenderName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(appenderName);
    }
}
