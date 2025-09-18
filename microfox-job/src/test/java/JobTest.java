import ir.moke.microfox.MicroFox;
import job.ByeJob;
import job.HelloJob;

public class JobTest {
    public static void main(String[] args) {
        MicroFox.job(new HelloJob(), "*/10 * * * * ? *", true);
        MicroFox.job(new ByeJob(), "*/5 * * * * ? *", false);
    }
}
