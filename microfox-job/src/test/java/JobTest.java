import ir.moke.microfox.MicroFox;

public class JobTest {
    public static void main(String[] args) {
        MicroFox.job(new ExampleJob(),"*/3 * * * * ? *");
    }
}
