import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Method;

public class HttpTest {

    public static void main(String[] args) {
        MicroFox.httpRouter("/hello", Method.GET, (request, response) -> response.body("Hello dear !"));
    }
}
