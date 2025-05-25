package rest;

import org.junit.jupiter.api.Test;

import ir.moke.microfox.MicroFoxServer;
import static ir.moke.microfox.MicroFox.*;

public class MicroFoxTest {
    public static void main(String[] str) {
        filter("/book/add", ((request, response) -> System.out.println("I'm Filter")));
        post("/book/add", (req, resp) -> {
            rest.Book body = req.body(rest.Book.class);
            rest.BookService.instance.add(body);
            resp.body(body);
        });

        get("/book/find", (request, response) -> response.body(rest.BookService.instance.find()));
        delete("/book/remove", (request, response) -> rest.BookService.instance.removeAll());

        /*------------------------------------------*/
        // http "http://localhost:8080/api/mahdi/12?id=23"
        get("/api/:name/:age", (request, response) -> {
            System.out.println("ID : " + request.queryParameter("id"));
            System.out.println("Name : " + request.pathParam("name"));
            System.out.println("Age : " + request.pathParam("age"));
        });

        /*------------------------------------------*/
        // http -F http://localhost:8080/redirect
        get("/target", (request, response) -> response.body("I'm target"));
        delete("/redirect", (request, response) -> response.redirect("/target"));

        MicroFoxServer.start();

        MicroFoxServer.start();
    }
}
