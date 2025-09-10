package ir.moke.microfox.exception;

import ir.moke.microfox.api.http.ErrorObject;

public interface ExceptionMapper<T extends Throwable> {
    ErrorObject toResponse(T throwable);
}
