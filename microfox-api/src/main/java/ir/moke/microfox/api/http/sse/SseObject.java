package ir.moke.microfox.api.http.sse;

public record SseObject(String data, String event, String id, Long retry) {
    public SseObject(String data, String event, String id) {
        this(data, event, id, null);
    }

    public SseObject(String data, String event) {
        this(data, event, null, null);
    }

    public SseObject(String data) {
        this(data, null, null, null);
    }
}
