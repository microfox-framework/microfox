package ir.moke.test.exception;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.ErrorObject;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.MicroFoxException;

public class MicroFoxExceptionMapper extends ExceptionMapper<MicroFoxException> {
    @Override
    public ErrorObject toResponse(MicroFoxException throwable) {
        return new ErrorObject.Builder()
                .setStatusCode(throwable.getStatusCode())
                .setContentType(ContentType.APPLICATION_JSON)
                .setStatusCode(StatusCode.BAD_GATEWAY)
                .setBody("Message : " + throwable.getStatusCode())
                .build();

    }
}
