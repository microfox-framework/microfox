package ir.moke.microfox.http;

import ir.moke.microfox.api.http.Filter;

public record FilterInfo(String path, int order, Filter filter) {
}