import ir.moke.microfox.MicroFoxConfig;
import ir.moke.microfox.http.HttpUtils;
import org.junit.jupiter.api.Test;

public class HttpUtilsTest {

    @Test
    public void checkRouteContextPath() {
        MicroFoxConfig.MICROFOX_HTTP_BASE_CONTEXT = "/api/v1";
        String path = "/hello";
        String string = HttpUtils.concatContextPath(path);
        System.out.println(string);
    }
}
