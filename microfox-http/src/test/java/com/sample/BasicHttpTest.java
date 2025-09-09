package com.sample;

import com.sample.exception.MyExceptionMapper;
import com.sample.resource.EchoEndpoint;
import com.sample.resource.EchoRoute;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class BasicHttpTest {
    private static final Logger logger = LoggerFactory.getLogger(BasicHttpTest.class);

    public static void main(String[] args) {
        MicroFox.registerExceptionMapper(new MyExceptionMapper());
        MicroFox.httpFilter("/api/*", BasicHttpTest::simpleFilter);
        MicroFox.httpRouter("/api/echo", Method.GET, new EchoRoute());
        MicroFox.websocket(EchoEndpoint.class);
    }

    private static boolean simpleFilter(Request req, Response resp) {
        logger.info("Receive message : {}", LocalTime.now());
        resp.body("Filter Executed\n");
        return true;
    }
}