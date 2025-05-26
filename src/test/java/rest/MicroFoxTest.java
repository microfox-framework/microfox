package rest;

import ir.moke.microfox.MicroFoxServer;

import static ir.moke.microfox.MicroFox.*;

public class MicroFoxTest {
    public static void main(String[] str) {
        filter("/book/:id", ((request, response) -> System.out.println("I'm Filter")));
        post("/book", (req, resp) -> {
            Book body = req.body(Book.class);
            BookService.save(body);
            resp.body(body);
        });

        get("/book", (request, response) -> response.body(BookService.find()));
        get("/book/:id", (request, response) -> {
            int id = Integer.parseInt(request.pathParam("id"));
            response.body(BookService.find(id));
        });
        delete("/book/:id", (request, response) -> BookService.remove(Integer.parseInt(request.pathParam("id"))));

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
    }
}
