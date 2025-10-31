package com.sample.resource;

import com.sample.exception.SampleException;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.api.http.Route;

public class RouteCheckException implements Route {

    @Override
    public void handle(Request request, Response response) {
        throw new SampleException("Oh, error occurred !");
    }
}
