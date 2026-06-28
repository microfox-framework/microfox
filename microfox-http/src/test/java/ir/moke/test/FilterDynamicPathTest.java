package ir.moke.test;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Chain;
import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;

public class FilterDynamicPathTest {

    static void main() {
//        MicroFox.httpFilter("/api/v1/{id}", 1, FilterDynamicPathTest::filter);
//        MicroFox.httpRouter("/api/v1/{id}", HttpMethod.GET, FilterDynamicPathTest::hello);

//        MicroFox.httpFilter("/api/v1/123", 1, FilterDynamicPathTest::filter);
//        MicroFox.httpRouter("/api/v1/123", HttpMethod.GET, FilterDynamicPathTest::hello);

        MicroFox.filter("/api/v1/{id}/*", 1, FilterDynamicPathTest::filter);
        MicroFox.route("/api/v1/{id}hello", HttpMethod.GET, FilterDynamicPathTest::hello);
    }

    private static void filter(Request request, Response response, Chain chain) {
        System.out.println("Filter Called");
        chain.doFilter(request, response);
    }

    private static void hello(Request request, Response response) {
        String id = request.pathParam("id");
        String msg = "Request Processed with id %s".formatted(id);
        System.out.println(msg);
        response.body(msg);
    }
}
