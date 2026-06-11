package ir.moke.test;

import ch.qos.logback.classic.Level;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Chain;
import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import ir.moke.test.exception.ExceptionController;
import ir.moke.test.exception.SampleException;
import ir.moke.test.resource.RouteCheckException;
import ir.moke.test.resource.RouteListUsers;
import ir.moke.test.resource.RouteLogin;
import ir.moke.test.resource.ws.EchoEndpoint;
import ir.moke.test.security.BasicAuthSecurity;
import ir.moke.test.security.JwtSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BasicHttpTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicHttpTest.class);

    static {
        MicroFox.logger(new ConsoleGenericModel("test", "ir.moke.test", Level.TRACE));
    }

    static void main(String[] args) {
        MicroFox.registerExceptionMapper(SampleException.class, ExceptionController::handleSampleException);
        MicroFox.registerExceptionMapper(MicroFoxException.class, ExceptionController::handleMicrofoxException);

        MicroFox.httpFilter("/api/*", -700, BasicHttpTest::simpleFilter);
        MicroFox.httpRouter("/api/login", HttpMethod.GET, new RouteLogin(), new BasicAuthSecurity());
        MicroFox.httpRouter("/api/users", HttpMethod.GET, new RouteListUsers(), new JwtSecurity(), List.of("ADMIN", "MEMBER"), List.of("read:users"));
        MicroFox.httpRouter("/api/error", HttpMethod.GET, new RouteCheckException());
        MicroFox.websocket(EchoEndpoint.class);
    }

    private static void simpleFilter(Request req, Response resp, Chain chain) {
        logger.info("Before chain");
        chain.doFilter(req, resp);
        logger.info("After chain");
    }
}