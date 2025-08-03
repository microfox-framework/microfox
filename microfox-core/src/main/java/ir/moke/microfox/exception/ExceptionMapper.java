package ir.moke.microfox.exception;

import ir.moke.microfox.api.http.ResponseObject;

public interface ExceptionMapper<T extends Throwable> {
    ResponseObject toResponse(T throwable);
}
