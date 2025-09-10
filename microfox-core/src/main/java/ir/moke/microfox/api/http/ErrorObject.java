package ir.moke.microfox.api.http;

import ir.moke.kafir.utils.JsonUtils;
import jakarta.servlet.http.Cookie;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorObject {
    private final StatusCode statusCode;
    private final ContentType contentType;
    private final Map<String, Object> headers;
    private final byte[] body;
    private final Locale locale;
    private final String characterEncoding;
    private final List<Cookie> cookies;

    private ErrorObject(Builder builder) {
        this.statusCode = builder.statusCode;
        this.contentType = builder.contentType;
        this.headers = builder.headers;
        this.body = builder.body;
        this.locale = builder.locale;
        this.characterEncoding = builder.characterEncoding;
        this.cookies = builder.cookies;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public static class Builder {
        private StatusCode statusCode;
        private ContentType contentType;
        private final Map<String, Object> headers = new ConcurrentHashMap<>();
        private Locale locale;
        private String characterEncoding;
        private List<Cookie> cookies;
        private byte[] body;

        public Builder setBody(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder setBody(Object o) {
            body = JsonUtils.toJson(o).getBytes(StandardCharsets.UTF_8);
            if (contentType == null) setContentType(ContentType.APPLICATION_JSON);
            return this;
        }

        public Builder setBody(String str) {
            body = str.getBytes(StandardCharsets.UTF_8);
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

        public Builder addHeader(String key, int value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder addHeader(String key, ZonedDateTime zonedDateTime) {
            this.headers.put(key, zonedDateTime.toEpochSecond());
            return this;
        }

        public Builder addHeader(String key, Date date) {
            this.headers.put(key, date.toInstant().getEpochSecond());
            return this;
        }

        public Builder addHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder setCharacterEncoding(String characterEncoding) {
            this.characterEncoding = characterEncoding;
            return this;
        }

        public Builder setCookies(List<Cookie> cookies) {
            this.cookies = cookies;
            return this;
        }

        public ErrorObject build() {
            return new ErrorObject(this);
        }
    }
}
