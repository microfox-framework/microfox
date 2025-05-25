import static ir.moke.MicroFox.*;

public class MicroFoxTest {
    public static void main(String[] args) {
        filter("/book",((request, response) -> System.out.println("I'm Filter")));
        post("/book",(req,resp) -> {
            Book body = req.body(Book.class);
            resp.body(body);
        });
    }
}
