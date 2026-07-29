import ch.qos.logback.classic.Level;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.job.JobOption;
import ir.moke.microfox.logger.model.ConsoleGenericModel;
import job.HelloJob;


void main() {
    MicroFox.logger(new ConsoleGenericModel("job", "job", Level.DEBUG));

    JobOption option = new JobOption.Builder().setDistributed(false).setAllowConcurrent(true).build();
    MicroFox.job(new HelloJob(), "Hello", "G1", "*/2 * * * * ? *", option);
//    MicroFox.job(new ByeJob(), "Hello", "G1", "*/3 * * * * ? *", option);
}
