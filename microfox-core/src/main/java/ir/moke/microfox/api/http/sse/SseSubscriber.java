package ir.moke.microfox.api.http.sse;

import ir.moke.microfox.api.http.Response;

import java.util.concurrent.Flow;

public class SseSubscriber implements Flow.Subscriber<SseObject> {
    private Flow.Subscription subscription;
    private final Response response;

    public SseSubscriber(Response response) {
        this.response = response;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(SseObject sseObject) {
        response.sse(sseObject);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        subscription.cancel();
    }

    @Override
    public void onComplete() {
        subscription.cancel();
    }
}
