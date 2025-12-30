package ir.moke.test.sse;

import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.http.ResourceHolder;

import java.time.LocalDateTime;
import java.util.TimerTask;
import java.util.concurrent.SubmissionPublisher;

public class SseTask extends TimerTask {
    @Override
    public void run() {
        SubmissionPublisher<SseObject> submissionPublisher = ResourceHolder.getSseByIdentity("sse-test");
        if (submissionPublisher != null) {
            submissionPublisher.submit(new SseObject("Hello: " + LocalDateTime.now()));
        }
    }
}
