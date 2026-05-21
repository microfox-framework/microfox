package ir.moke.microfox.api.http;

import java.io.Serializable;

@FunctionalInterface
public interface Route extends Serializable {

    void handle(Request request, Response response) throws Throwable;
}
