import ir.moke.microfox.MicroFox;

public class MainClass {
    public static void main(String[] args) {
        MicroFox.httpGet("/hello", (request, response) -> System.out.println("Hello"));
    }
}
