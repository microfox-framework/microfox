package ir.moke.microfox.http;

import ir.moke.microfox.MicroFoxConfig;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpContainer {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpContainer.class);
    private static final String contextPath = MicroFoxConfig.MICROFOX_HTTP_BASE_CONTEXT;
    private static final String baseDir = "/tmp/tomcat";

    static {
        try {
            // Disable tomcat logs
            Logger.getLogger("org.apache").setLevel(Level.OFF);
            Logger.getLogger("org.apache.catalina").setLevel(Level.OFF);
            Logger.getLogger("org.apache.tomcat").setLevel(Level.OFF);
            Logger.getLogger("org.apache.jasper").setLevel(Level.OFF);
            Logger.getLogger("org.apache.coyote").setLevel(Level.OFF);

            Files.createDirectory(Path.of(baseDir));
        } catch (IOException ignore) {
        }
    }

    public static void start() {
        try {
            var tomcat = new Tomcat();
            tomcat.setConnector(createHttpConnector());

            var context = tomcat.addWebapp(contextPath, baseDir);

            // add websockets
//            context.addServletContainerInitializer(new WsSci(), new HashSet<>(List.of(SampleWebSocket.class)));

            // add servlets
            context.addServletContainerInitializer(new EmbeddedServletContainerInitializer(), null);

            /*
             * add filters
             * Note : Filter could be ordered by @WebFilter#filterName method
             * */
            context.addServletContainerInitializer(new EmbeddedFilterContainerInitializer(), Set.of(BaseFilter.class));

            tomcat.start();
            tomcat.getServer().await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connector createHttpConnector() {
        String port = MicroFoxConfig.MICROFOX_HTTP_PORT;
        logger.info("HTTP connector is ready, listening to port 8080");
        Connector connector = new Connector();
        connector.setProperty("address", MicroFoxConfig.MICROFOX_HTTP_HOST);
        connector.setPort(Integer.parseInt(port));
        return connector;
    }
}
