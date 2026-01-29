package ir.moke.microfox.http.filter;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.ErrorObject;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.MicroFoxException;

public class MicroFoxExceptionMapper extends ExceptionMapper<MicroFoxException> {
    @Override
    public ErrorObject toResponse(MicroFoxException exception) {
        StatusCode statusCode = exception.getStatusCode();
        return new ErrorObject.Builder()
                .setStatusCode(statusCode)
                .setBody(exception.getMessage() != null ? exception.getMessage() : "")
                .setContentType(ContentType.APPLICATION_JSON)
                .build();
    }
}
