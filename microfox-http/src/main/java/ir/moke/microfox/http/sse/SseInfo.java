package ir.moke.microfox.http.sse;

import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.http.ResourceHolder;

import java.io.Closeable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class SseInfo implements Closeable {
    private SubmissionPublisher<SseObject> publisher;
    private String identity;
    private String path;
    private Map<String, String[]> headers;
    private Map<String, String[]> queryParameters;

    public SseInfo() {
    }

    public SseInfo(String identity, String path) {
        this.identity = identity;
        this.path = path;
        this.publisher = new SubmissionPublisher<>(ResourceHolder.SSE_EXECUTOR, Flow.defaultBufferSize());
    }

    public SubmissionPublisher<SseObject> getPublisher() {
        return publisher;
    }

    public void setPublisher(SubmissionPublisher<SseObject> publisher) {
        this.publisher = publisher;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String[]> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String[]> headers) {
        this.headers = headers;
    }

    public Map<String, String[]> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String[]> queryParameters) {
        this.queryParameters = queryParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SseInfo other)) return false;
        return Objects.equals(identity, other.identity)
                && Objects.equals(path, other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, path);
    }

    @Override
    public void close() {
        publisher.close();
    }
}
