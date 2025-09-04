package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

public class ConsoleLogInfo extends LogInfo {
    private String pattern;

    public ConsoleLogInfo(String appenderName, String packageName, Level level) {
        super(appenderName, packageName, level);
    }

    public ConsoleLogInfo(String name, String packageName, Level level, String pattern) {
        super(name, packageName, level);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
