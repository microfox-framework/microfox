package com.sample;

import com.sample.exception.MyExceptionMapper;
import com.sample.resource.EchoEndpoint;
import com.sample.resource.EchoRoute;
import com.sample.sse.SseTask;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.sse.SseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.Timer;

public class HttpTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpTest.class);

    public static void main(String[] args) {
        MicroFox.registerExceptionMapper(new MyExceptionMapper());
        MicroFox.httpFilter("/api/*", HttpTest::simpleFilter);
        MicroFox.httpRouter("/api/echo", Method.GET, new EchoRoute());
        MicroFox.websocket(EchoEndpoint.class);


        /*
        * Install httpie package
        * run this command :
        * http --stream http://localhost:8080/api/sse 'Accept: text/event-stream'
        * */
        MicroFox.sseRegister("sse-test", "/api/sse");
        MicroFox.ssePublisher("sse-test", () -> new SseObject("Hello"));
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new SseTask(), 2000, 3000);
    }

    private static boolean simpleFilter(Request req, Response resp) {
        logger.info("Receive message : {}", LocalTime.now());
        resp.body("Filter Executed\n");
        return true;
    }
}