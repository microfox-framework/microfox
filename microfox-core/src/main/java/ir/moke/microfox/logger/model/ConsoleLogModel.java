package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

public class ConsoleLogModel extends LogModel {
    private String pattern;

    public ConsoleLogModel(String appenderName, String packageName, Level level) {
        super(appenderName, packageName, level);
    }

    public ConsoleLogModel(String name, String packageName, Level level, String pattern) {
        super(name, packageName, level);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
