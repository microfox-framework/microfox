package ir.moke.microfox.http;

import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.http.servlet.MetricServlet;
import ir.moke.microfox.http.servlet.OpenApiServlet;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpContainer {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpContainer.class);
    private static final String contextPath = "/";
    private static final String baseDir = "/tmp/tomcat";
    private static final Integer HTTP_PORT = Integer.parseInt(HttpContainerConfig.MICROFOX_HTTP_PORT);
    private static final Integer HTTPS_PORT = Integer.parseInt(HttpContainerConfig.MICROFOX_HTTPS_PORT);
    private static final boolean FORCE_REDIRECT = Boolean.parseBoolean(HttpContainerConfig.MICROFOX_FORCE_REDIRECT_HTTPS);

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
            if (HttpContainerConfig.MICROFOX_HTTP_BASE_API.length() > 1 && (!HttpContainerConfig.MICROFOX_HTTP_BASE_API.startsWith("/") || HttpContainerConfig.MICROFOX_HTTP_BASE_API.endsWith("/"))) {
                throw new MicrofoxException("Base api path must start with '/' and must not end with '/'. Example: '/api/v1'");
            }
            var tomcat = new Tomcat();
            tomcat.getService().addConnector(createHttpConnector());
            tomcat.getService().addConnector(createHttpsConnector());

            var context = tomcat.addWebapp(contextPath, baseDir);
            if (FORCE_REDIRECT) addSecurityConstraint(context);

            // add websockets
//            context.addServletContainerInitializer(new WsSci(), new HashSet<>(List.of(SampleWebSocket.class)));

            // add servlets
            context.addServletContainerInitializer(new EmbeddedServletContainerInitializer(), null);

            /*
             * add filters
             * Note : Filter could be ordered by @WebFilter#filterName method
             * */
            Set<Class<?>> classes = Set.of(BaseFilter.class, OpenApiServlet.class, MetricServlet.class);
            context.addServletContainerInitializer(new EmbeddedFilterContainerInitializer(), classes);

            tomcat.start();
            tomcat.getServer().await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Connector createHttpConnector() {
        Connector connector = new Connector();
        connector.setProperty("address", HttpContainerConfig.MICROFOX_HTTP_HOST);
        connector.setPort(HTTP_PORT);
        if (FORCE_REDIRECT) connector.setRedirectPort(HTTPS_PORT);
        logger.info("HTTP connector is ready, listening to port {}", HTTP_PORT);
        return connector;
    }

    public static Connector createHttpsConnector() {
        SSLHostConfig sslHostConfig = getSslHostConfig();
        Connector connector = new Connector();
        connector.setPort(HTTPS_PORT);
        connector.setSecure(true);
        connector.setScheme("https");
        connector.setProperty("SSLEnabled", "true");
        connector.addSslHostConfig(sslHostConfig);
        logger.info("HTTPS connector is ready, listening to port {}", HTTPS_PORT);
        return connector;
    }

    private static SSLHostConfig getSslHostConfig() {
        String keystorePassword = "tompass";
        String keystoreAliasName = "tomcat-embedded";

        // by default generated with keytool
        Path keystoreFile = Path.of("/tmp/application.keystore");

        // Generate pkcs12 keystore programmatically if jks does not exist
        if (!Files.exists(keystoreFile)) {
            KeystoreUtils.createPKCS12(keystoreFile, keystorePassword, keystoreAliasName, null);
        }
        SSLHostConfig sslHostConfig = new SSLHostConfig();
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);
        certificate.setCertificateKeystoreFile(keystoreFile.toFile().getAbsolutePath());
        certificate.setCertificateKeystorePassword(keystorePassword);
        certificate.setCertificateKeyAlias(keystoreAliasName);
        sslHostConfig.addCertificate(certificate);
        return sslHostConfig;
    }

    private static void addSecurityConstraint(Context context) {
        SecurityConstraint securityConstraint = new SecurityConstraint();
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/*");
        securityConstraint.addCollection(collection);

        // Enforce HTTPS
        securityConstraint.setUserConstraint("CONFIDENTIAL");
        context.addConstraint(securityConstraint);
    }
}
