package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

public class ConsoleGenericModel extends GenericModel {
    private String pattern;

    public ConsoleGenericModel(String appenderName, String packageName, Level level) {
        super(appenderName, packageName, level);
    }

    public ConsoleGenericModel(String name, String packageName, Level level, String pattern) {
        super(name, packageName, level);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
