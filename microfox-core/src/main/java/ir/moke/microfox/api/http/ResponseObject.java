package ir.moke.microfox.api.http;

import java.util.Map;

public class ResponseObject {
    private StatusCode statusCode;
    private ContentType contentType;
    private Map<String, String> headers;
    private byte[] body;

    private ResponseObject() {
    }

    public ResponseObject(Builder builder) {
        this.statusCode = builder.statusCode;
        this.contentType = builder.contentType;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public static class Builder {
        private StatusCode statusCode;
        private ContentType contentType;
        private Map<String, String> headers;
        private byte[] body;

        public Builder setBody(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder setStatusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder setContentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public ResponseObject build() {
            return new ResponseObject(this);
        }
    }
}
