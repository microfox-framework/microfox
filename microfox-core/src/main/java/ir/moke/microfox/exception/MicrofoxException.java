package ir.moke.microfox.exception;

public class MicrofoxException extends RuntimeException {
    public MicrofoxException(String message) {
        super(message);
    }

    public MicrofoxException(String message, Throwable e) {
        super(message, e);
    }

    public MicrofoxException(Throwable cause) {
        super(cause);
    }
}
