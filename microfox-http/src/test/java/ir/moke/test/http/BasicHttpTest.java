package ir.moke.test.http;

import ch.qos.logback.classic.Level;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.*;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import ir.moke.test.exception.ExceptionController;
import ir.moke.test.exception.SampleException;
import ir.moke.test.http.resource.RouteCheckException;
import ir.moke.test.http.resource.RouteListUsers;
import ir.moke.test.http.resource.RouteLogin;
import ir.moke.test.http.resource.ws.EchoEndpoint;
import ir.moke.test.http.security.BasicAuthSecurity;
import ir.moke.test.http.security.JwtSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ir.moke.microfox.MicroFox.exceptionMapperRegister;

public class BasicHttpTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicHttpTest.class);

    static {
        MicroFox.logger(new ConsoleGenericModel("test", "ir.moke.test", Level.TRACE));
    }

    static void main(String[] args) {
        exceptionMapperRegister(SampleException.class, ExceptionController::handleSampleException);
        exceptionMapperRegister(MicroFoxException.class, ExceptionController::handleMicroFoxException);
        MicroFox.security(new SecurityInfo("/api/login", new BasicAuthSecurity(), 1));
        MicroFox.security(new SecurityInfo("/api/users", new JwtSecurity(), 2));

        MicroFox.filter("/api/*", -700, BasicHttpTest::simpleFilter);
        MicroFox.route("/api/login", HttpMethod.GET, new RouteLogin());
        MicroFox.route("/api/users", HttpMethod.GET, new RouteListUsers(), List.of("ADMIN", "MEMBER"), List.of("read:users"));
        MicroFox.route("/api/error", HttpMethod.GET, new RouteCheckException());
        MicroFox.websocket(EchoEndpoint.class);
    }

    private static void simpleFilter(Request req, Response resp, Chain chain) {
        logger.info("Before chain");
        chain.doFilter(req, resp);
        logger.info("After chain");
    }
}