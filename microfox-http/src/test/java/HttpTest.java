import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Method;

public class HttpTest {

    public static void main(String[] args) {
        MicroFox.registerExceptionMapper(new MyExceptionMapper());
        MicroFox.httpFilter("/api/*", (request, response) -> response.body("Filter Executed\n"));
        MicroFox.httpRouter("/api/hello", Method.GET, (request, response) -> response.body("Hello dear !\n"));
    }
}
