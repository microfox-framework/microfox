package ir.moke.test.exception;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.ErrorObject;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.exception.MicroFoxException;

import java.nio.charset.StandardCharsets;

public class ExceptionController {

    public static ErrorObject handleSampleException(Throwable throwable) {
        String message = throwable.getMessage();
        return new ErrorObject.Builder()
                .setContentType(ContentType.APPLICATION_JSON)
                .setStatusCode(StatusCode.BAD_GATEWAY)
                .setBody(message.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    public static ErrorObject handleMicrofoxException(Throwable throwable) {
        MicroFoxException exception = (MicroFoxException) throwable;
        return new ErrorObject.Builder()
                .setStatusCode(exception.getStatusCode())
                .setContentType(ContentType.APPLICATION_JSON)
                .setStatusCode(exception.getStatusCode())
                .setBody(exception.getMessage())
                .build();
    }
}
