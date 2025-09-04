import ir.moke.microfox.system.StatisticTask;
import org.junit.jupiter.api.Test;

public class StatisticsTest {

    @Test
    public void check() {
        StatisticTask.send();
    }
}
