import ir.moke.microfox.MicroFox;

public class JobTest {
    public static void main(String[] args) {
        MicroFox.job(new HelloJob(), "*/10 * * * * ? *", true);
        MicroFox.job(new ByeJob(), "*/5 * * * * ? *", false);
    }
}
