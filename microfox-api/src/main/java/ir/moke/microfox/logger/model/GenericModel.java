package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

import java.util.Objects;

public abstract class GenericModel implements LogModel {
    private final String appenderName;
    private final String packageName;
    private final Level level;

    GenericModel(String appenderName, String packageName, Level level) {
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
        GenericModel genericModel = (GenericModel) o;
        return Objects.equals(appenderName, genericModel.appenderName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(appenderName);
    }
}
