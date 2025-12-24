package ir.moke.microfox.http.sse;

import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.sse.SseObject;
import jakarta.servlet.AsyncContext;

import java.util.concurrent.Flow;

public class SseSubscriber implements Flow.Subscriber<SseObject> {
    private Flow.Subscription subscription;
    private final Response response;
    private final AsyncContext asyncContext;
    private final SseInfo sseInfo;

    public SseSubscriber(Response response, AsyncContext asyncContext, SseInfo sseInfo) {
        this.response = response;
        this.asyncContext = asyncContext;
        this.sseInfo = sseInfo;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(SseObject sseObject) {
        try {
            response.sse(sseObject);
            subscription.request(1);
        } catch (Exception e) {
            close();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        close();
    }

    @Override
    public void onComplete() {
        close();
    }

    private void close() {
        subscription.cancel();
        asyncContext.complete();
        sseInfo.close();
    }
}
