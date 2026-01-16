package ir.moke.test;

import ch.qos.logback.classic.Level;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Chain;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import ir.moke.test.exception.MyExceptionMapper;
import ir.moke.test.resource.RouteCheckException;
import ir.moke.test.resource.RouteListUsers;
import ir.moke.test.resource.RouteLogin;
import ir.moke.test.resource.ws.EchoEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicHttpTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicHttpTest.class);

    static {
        MicroFox.logger(new ConsoleGenericModel("test","com.sample", Level.TRACE));
    }

    public static void main(String[] args) {
        MicroFox.registerExceptionMapper(new MyExceptionMapper());
        MicroFox.httpFilter("/api/*", BasicHttpTest::simpleFilter);
        MicroFox.httpRouter("/api/login", Method.GET, new RouteLogin());
        MicroFox.httpRouter("/api/users", Method.GET, new RouteListUsers());
        MicroFox.httpRouter("/api/error", Method.GET, new RouteCheckException());
        MicroFox.websocket(EchoEndpoint.class);
    }

    private static void simpleFilter(Request req, Response resp, Chain chain) {
        logger.info("Before chain");
        chain.doFilter(req,resp);
        logger.info("After chain");
    }
}