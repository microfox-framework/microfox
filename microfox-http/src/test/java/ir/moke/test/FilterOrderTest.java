package ir.moke.test;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Chain;
import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;

public class FilterOrderTest {

    static void main() {
        MicroFox.filter("/check", 5, "F1", "Test", FilterOrderTest::F1);
        MicroFox.filter("/check", 1, "F2", "Test", FilterOrderTest::F2);
        MicroFox.filter("/check", 2, "F3", "Test", FilterOrderTest::F3);
        MicroFox.route("/check", HttpMethod.GET, "check", "test", ((_, _) -> System.out.println("Checked")));
    }

    private static void F1(Request request, Response response, Chain chain) {
        System.out.println("F1 Called");
        chain.doFilter(request, response);
    }

    private static void F2(Request request, Response response, Chain chain) {
        System.out.println("F2 Called");
        chain.doFilter(request, response);
    }

    private static void F3(Request request, Response response, Chain chain) {
        System.out.println("F3 Called");
        chain.doFilter(request, response);
    }
}
