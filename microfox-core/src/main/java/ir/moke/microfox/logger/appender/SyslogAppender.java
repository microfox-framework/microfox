package ir.moke.microfox.logger.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ir.moke.microfox.logger.model.SysGenericModel;
import ir.moke.microfox.utils.LogUtils;
import org.slf4j.LoggerFactory;

public class SyslogAppender {
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    public static void addSyslogLogger(SysGenericModel log) {
        addSyslogLogger(
                log.getAppenderName(),
                log.getPackageName(),
                log.getLevel(),
                log.getHost(),
                log.getPort(),
                log.getFacility(),
                log.getPattern()
        );
    }

    public static void addSyslogLogger(String name,
                                       String packageName,
                                       Level level,
                                       String host,
                                       int port,
                                       SysGenericModel.Facility facility,
                                       String pattern) {
        ch.qos.logback.classic.net.SyslogAppender syslogAppender = getSyslogAppender(name, host, port, facility, pattern);
        Logger log = loggerContext.getLogger(packageName);
        LogUtils.setFilter(level, syslogAppender);
        log.setAdditive(false);
        log.addAppender(syslogAppender);
    }

    private static ch.qos.logback.classic.net.SyslogAppender getSyslogAppender(String name,
                                                                               String host,
                                                                               int port,
                                                                               SysGenericModel.Facility facility,
                                                                               String pattern) {
        ch.qos.logback.classic.net.SyslogAppender syslogAppender = new ch.qos.logback.classic.net.SyslogAppender();
        syslogAppender.setContext(loggerContext);
        syslogAppender.setName(name);
        syslogAppender.setSyslogHost(host);
        syslogAppender.setPort(port);
        syslogAppender.setFacility(facility.name());
        syslogAppender.setSuffixPattern(pattern != null ? pattern : LogUtils.DEFAULT_SYSLOG_PATTERN);
        syslogAppender.start();
        return syslogAppender;
    }
}
