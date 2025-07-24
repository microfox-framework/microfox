import ir.moke.microfox.MicroFox;

public class HttpTest {

    public static void main(String[] args) {
        MicroFox.httpGet("/hello", (request, response) -> response.body("Hello dear !"));
    }
}
