import ir.moke.microfox.MicroFoxServer;
import org.junit.jupiter.api.Test;

import static ir.moke.microfox.MicroFox.*;

public class MicroFoxTest {

    @Test
    public void checkPathParameter() {
        get("/api/:name/:age", (request, response) -> {
            System.out.println("ID : " + request.queryParameter("id"));
            System.out.println("Name : " + request.pathParam("name"));
            System.out.println("Age : " + request.pathParam("age"));
        });

        MicroFoxServer.start();
    }

    @Test
    public void checkRedirect() {
        get("/target", (request, response) -> response.body("I'm target"));
        delete("/redirect", (request, response) -> response.redirect("/target"));

        MicroFoxServer.start();
    }

    @Test
    public void checkMultipleService() {
        filter("/book/add", ((request, response) -> System.out.println("I'm Filter")));
        post("/book/add", (req, resp) -> {
            Book body = req.body(Book.class);
            BookService.instance.add(body);
            resp.body(body);
        });

        get("/book/find", (request, response) -> response.body(BookService.instance.find()));
        delete("/book/remove", (request, response) -> BookService.instance.removeAll());

        MicroFoxServer.start();
    }
}
