package ir.moke.microfox.http.filter;

import ir.moke.microfox.api.http.ErrorObject;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.MicroFoxException;

public class MicroFoxExceptionMapper extends ExceptionMapper<MicroFoxException> {
    @Override
    public ErrorObject toResponse(MicroFoxException exception) {
        StatusCode statusCode = exception.getStatusCode();
        return new ErrorObject.Builder().setStatusCode(statusCode).build();
    }
}
