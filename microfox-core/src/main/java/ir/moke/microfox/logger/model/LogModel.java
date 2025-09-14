package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

public interface LogModel {
    String getPackageName();

    Level getLevel();

    String getAppenderName();
}
