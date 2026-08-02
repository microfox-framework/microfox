package ir.moke.test.sse;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.sse.SseObject;

import java.time.LocalDateTime;
import java.util.TimerTask;

public class SseTask extends TimerTask {
    @Override
    public void run() {
        MicroFox.ssePublisher("sse-test", new SseObject("Hello: " + LocalDateTime.now()));
    }
}
