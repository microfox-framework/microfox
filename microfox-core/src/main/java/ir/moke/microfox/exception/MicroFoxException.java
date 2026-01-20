package ir.moke.microfox.exception;

import ir.moke.microfox.api.http.StatusCode;

public class MicroFoxException extends RuntimeException {
    private StatusCode statusCode;

    public MicroFoxException(String message) {
        super(message);
    }

    public MicroFoxException(String message, Throwable e) {
        super(message, e);
    }

    public MicroFoxException(Throwable cause) {
        super(cause);
    }

    public MicroFoxException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}
