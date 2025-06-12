package ir.moke.microfox.api.http.sse;

import java.util.Objects;
import java.util.concurrent.SubmissionPublisher;

public class SseInfo {
    private SubmissionPublisher<SseObject> publisher;
    private String identity;
    private String path ;

    public SseInfo() {
    }

    public SseInfo(String identity, String path) {
        this.identity = identity;
        this.path = path;
        this.publisher = new SubmissionPublisher<>();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SseInfo sseInfo = (SseInfo) o;
        return Objects.equals(path, sseInfo.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }
}
