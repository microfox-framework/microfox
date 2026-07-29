package ir.moke.microfox.logger.model;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ir.moke.microfox.utils.LogUtils;

public class SysGenericModel extends GenericModel {
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
        LOCAL7;
    }

    private final String host;
    private final int port;
    private final Facility facility;
    private String pattern;

    public SysGenericModel(String appenderName, String packageName, Level level, String host, int port, Facility facility, Encoder<ILoggingEvent> encoder) {
        super(appenderName, packageName, level, encoder);
        this.facility = facility;
        this.port = port;
        this.host = host;
    }

    public SysGenericModel(String appenderName, String packageName, Level level, String host, int port, Facility facility) {
        super(appenderName, packageName, level, LogUtils.getEncoder());
        this.facility = facility;
        this.port = port;
        this.host = host;
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
