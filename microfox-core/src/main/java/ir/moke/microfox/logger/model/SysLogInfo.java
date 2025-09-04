package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;

public class SysLogInfo extends LogInfo {
    public enum Facility {
        USER,
        MAIL,
        DAEMON,
        AUTH,
        SYSLOG,
        LPR,
        NEWS,
        UUCP,
        CRON,
        AUTHPRIV,
        FTP,
        NTP,
        AUDIT,
        ALERT,
        CLOCK,
        LOCAL0,
        LOCAL1,
        LOCAL2,
        LOCAL3,
        LOCAL4,
        LOCAL5,
        LOCAL6,
        LOCAL7
    }

    private final String host;
    private final int port;
    private final Facility facility;
    private String pattern;

    public SysLogInfo(String appenderName, String packageName, Level level, String host, int port, Facility facility) {
        super(appenderName, packageName, level);
        this.facility = facility;
        this.port = port;
        this.host = host;
    }

    public SysLogInfo(String name, String packageName, Level level, String host, int port, Facility facility, String pattern) {
        super(name, packageName, level);
        this.host = host;
        this.port = port;
        this.facility = facility;
        this.pattern = pattern;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Facility getFacility() {
        return facility;
    }

    public String getPattern() {
        return pattern;
    }
}
