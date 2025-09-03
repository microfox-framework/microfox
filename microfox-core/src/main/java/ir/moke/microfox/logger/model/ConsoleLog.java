package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

public class ConsoleLog extends BaseLog {
    private String pattern;

    public ConsoleLog(String appenderName, String packageName, Level level) {
        super(appenderName, packageName, level);
    }

    public ConsoleLog(String name, String packageName, Level level, String pattern) {
        super(name, packageName, level);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
