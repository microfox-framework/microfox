package ir.moke.test;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.utils.StringUtils;

public class TestParameter {
    static void main() {
        MicroFox.route("/api/check", HttpMethod.GET, TestParameter::check);
    }

    public static void check(Request req, Response resp) {
        Integer age = req.queryParameter("age", StringUtils::isAlphabetic, Integer::parseInt, 12);
//        String name = req.queryParameter("name", StringUtils::isAlphabetic,e -> "Hello " + e,() -> "Hey supplier working !");
//        String name = req.queryParameterThrowable("name", StringUtils::isNumeric,e -> "Hello " + e, "Invalid parameter");
//        String name = req.queryParameterThrowable("name", StringUtils::isNumeric,e -> "Hello " + e, () -> new InvalidParameterException("Invalid parameter"));
        System.out.println(age);
    }
}
