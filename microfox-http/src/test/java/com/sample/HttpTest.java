package com.sample;

import ch.qos.logback.classic.Level;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.logger.LoggerManager;
import ir.moke.microfox.logger.model.ConsoleLog;
import ir.moke.microfox.logger.model.FileLog;
import ir.moke.microfox.logger.model.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class HttpTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpTest.class);

    static {
        ConsoleLog consoleLog = new ConsoleLog("sample-console", "com.sample", Level.TRACE);
        FileLog fileLog = new FileLog("sample", "com.sample", Level.TRACE, " /tmp/output.log", "%d{yyyy-MM-dd}.%i.log.gz", "1KB", 3, "1MB");
        SysLog sysLog = new SysLog("syslog-sample", "com.sample", Level.TRACE, "127.0.0.1", 514, SysLog.Facility.LOCAL2);
        LoggerManager.registerLog(fileLog);
        LoggerManager.registerLog(consoleLog);
        LoggerManager.registerLog(sysLog);
    }

    public static void main(String[] args) {
        MicroFox.registerExceptionMapper(new MyExceptionMapper());
        MicroFox.httpFilter("/api/*", (request, response) -> {
            logger.info("Receive message : {}", LocalTime.now());
            response.body("Filter Executed\n");
        });
        MicroFox.httpRouter("/api/hello", Method.GET, (request, response) -> response.body("Hello dear !\n"));
        MicroFox.websocket(EchoEndpoint.class);
    }
}
