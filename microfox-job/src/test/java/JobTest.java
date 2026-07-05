import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.job.JobOption;
import job.ByeJob;
import job.HelloJob;

public class JobTest {
    public static void main(String[] args) {
        JobOption option = new JobOption.Builder().setDistributed(false).setAllowConcurrent(true).build();
        MicroFox.job(new HelloJob(), "Hello", "G1", "*/10 * * * * ? *", option);
        MicroFox.job(new ByeJob(), "Bye", "G1", "*/5 * * * * ? *", option);
    }
}
