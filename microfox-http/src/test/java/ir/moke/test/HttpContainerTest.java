package ir.moke.test;

import ir.moke.microfox.MicroFox;

public class HttpContainerTest {

    public static void main(String[] args) {
        MicroFox.httpGet("/api/v1/hello",(request, response) -> {
            System.out.println("Hello dear !");
            response.body("Hello");
        });
    }
}
