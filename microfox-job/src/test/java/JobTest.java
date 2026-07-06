import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.job.JobOption;
import job.ByeJob;
import job.HelloJob;

public class JobTest {
    public static void main(String[] args) {
        JobOption option = new JobOption.Builder().setDistributed(false).setAllowConcurrent(true).build();
        MicroFox.job(new HelloJob(), "Hello", "G1", "*/2 * * * * ? *", option);
        MicroFox.job(new ByeJob(), "Hello", "G1", "*/3 * * * * ? *", option);
    }
}
