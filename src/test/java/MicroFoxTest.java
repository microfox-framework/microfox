import ir.moke.kafir.annotation.GET;
import ir.moke.microfox.MicroFox;

import java.net.http.HttpResponse;

public class MicroFoxTest {
    private interface BookService {
        @GET("/book/find")
        HttpResponse<String> findBooks();
    }

    public static void main(String[] str) throws Exception {
        MicroFox.restCall("dsa", null, BookService.class, bookService -> printResult(bookService.findBooks()));
    }

    private static void printResult(HttpResponse<String> response) {
        System.out.println(response.body());
    }
}
