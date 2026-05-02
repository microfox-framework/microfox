package ir.moke.microfox.exception;

import ir.moke.microfox.api.http.ErrorObject;

@FunctionalInterface
public interface ExceptionMapper {
    <T extends Throwable> ErrorObject handle(Throwable t) throws Throwable;
}
