import ir.moke.microfox.MicroFoxServer;

import static ir.moke.microfox.MicroFox.*;

public class MicroFoxTest {
    public static void main(String[] args) {
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
