package ir.moke.microfox.exception;

public class MicroFoxParameterException extends RuntimeException {

    public MicroFoxParameterException(String message) {
        super(message);
    }

    public MicroFoxParameterException(String message, Throwable e) {
        super(message, e);
    }
}
