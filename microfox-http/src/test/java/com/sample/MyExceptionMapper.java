package com.sample;

import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.ResponseObject;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.exception.ExceptionMapper;

import java.nio.charset.StandardCharsets;

public class MyExceptionMapper implements ExceptionMapper<SampleException> {
    @Override
    public ResponseObject toResponse(SampleException throwable) {
        String message = throwable.getMessage();
        return new ResponseObject.Builder()
                .setContentType(ContentType.APPLICATION_JSON)
                .setStatusCode(StatusCode.BAD_GATEWAY)
                .setBody(message.getBytes(StandardCharsets.UTF_8))
                .build();

    }
}
