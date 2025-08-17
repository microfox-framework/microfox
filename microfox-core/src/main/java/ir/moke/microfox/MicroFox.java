package ir.moke.microfox;

import ir.moke.kafir.http.Kafir;
import ir.moke.microfox.api.discovery.ServiceDiscoveryProvider;
import ir.moke.microfox.api.elastic.ElasticProvider;
import ir.moke.microfox.api.elastic.ElasticRepository;
import ir.moke.microfox.api.ftp.FtpFile;
import ir.moke.microfox.api.ftp.FtpProvider;
import ir.moke.microfox.api.ftp.MicroFoxFtpConfig;
import ir.moke.microfox.api.http.Filter;
import ir.moke.microfox.api.http.HttpProvider;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsProvider;
import ir.moke.microfox.api.job.JobProvider;
import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.api.kafka.KafkaProvider;
import ir.moke.microfox.api.kafka.KafkaStreamController;
import ir.moke.microfox.api.metrics.MetricsProvider;
import ir.moke.microfox.api.mybatis.MyBatisProvider;
import ir.moke.microfox.api.openapi.OpenApiProvider;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.ExceptionMapperHolder;
import ir.moke.microfox.healthcheck.MicroFoxHealthCheckService;
import ir.moke.microfox.utils.HttpClientConfig;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;

import java.io.File;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MicroFox {
    private static final HttpProvider httpProvider = ServiceLoader.load(HttpProvider.class).findFirst().orElse(null);
    private static final JobProvider jobProvider = ServiceLoader.load(JobProvider.class).findFirst().orElse(null);
    private static final FtpProvider ftpProvider = ServiceLoader.load(FtpProvider.class).findFirst().orElse(null);
    private static final MyBatisProvider myBatisProvider = ServiceLoader.load(MyBatisProvider.class).findFirst().orElse(null);
    private static final JpaProvider jpaProvider = ServiceLoader.load(JpaProvider.class).findFirst().orElse(null);
    private static final JmsProvider jmsProvider = ServiceLoader.load(JmsProvider.class).findFirst().orElse(null);
    private static final KafkaProvider kafkaProvider = ServiceLoader.load(KafkaProvider.class).findFirst().orElse(null);
    private static final ElasticProvider elasticProvider = ServiceLoader.load(ElasticProvider.class).findFirst().orElse(null);
    private static final OpenApiProvider openApiProvider = ServiceLoader.load(OpenApiProvider.class).findFirst().orElse(null);
    private static final MetricsProvider metricsProvider = ServiceLoader.load(MetricsProvider.class).findFirst().orElse(null);
    private static final ServiceDiscoveryProvider serviceDiscoveryProvider = ServiceLoader.load(ServiceDiscoveryProvider.class).findFirst().orElse(null);

    static {
        MicrofoxEnvironment.introduce();
        MicroFoxHealthCheckService.start();
        if (openApiProvider != null) openApiProvider.registerOpenAPI();
        if (metricsProvider != null) metricsProvider.registerMetrics();
        if (serviceDiscoveryProvider != null) serviceDiscoveryProvider.registerServiceDiscovery();
    }

    public static <T extends Throwable, E> void registerExceptionMapper(ExceptionMapper<T>... mappers) {
        for (ExceptionMapper<T> mapper : mappers) {
            ExceptionMapperHolder.add(mapper);
        }
    }

    public static void httpFilter(String path, Filter... filters) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.filter(path, filters);
    }

    public static void httpRouter(String path, Method method, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.http(path, method, route);
    }

    public static void sseRegister(String identity, String path) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.sseRegister(identity, path);
    }

    public static void ssePublisher(String identity, Supplier<SseObject> supplier) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.ssePublisher(identity, supplier);
    }

    public static void job(Runnable task, String cronExpression) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.job(task, cronExpression);
    }

    public static void job(Runnable task, ZonedDateTime zonedDateTime) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.job(task, zonedDateTime);
    }

    public static void ftpDownload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, Path localDownloadDir) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpDownload(microFoxFtpConfig, remoteFilePath, localDownloadDir);
    }

    public static void ftpBatchDownload(MicroFoxFtpConfig microFoxFtpConfig, List<String> remoteFilePath, Path localDownloadDir) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpBatchDownload(microFoxFtpConfig, remoteFilePath, localDownloadDir);
    }

    public static void ftpUpload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, File file) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpUpload(microFoxFtpConfig, remoteFilePath, file);
    }

    public static void ftpBatchUpload(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, List<File> files) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpBatchUpload(microFoxFtpConfig, remoteFilePath, files);
    }

    public static void ftpDelete(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpDelete(microFoxFtpConfig, remoteFilePath);
    }

    public static void ftpList(MicroFoxFtpConfig microFoxFtpConfig, String remoteFilePath, Consumer<List<FtpFile>> consumer) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpList(microFoxFtpConfig, remoteFilePath, consumer);
    }

    public static <T> T mybatis(String identity, Class<T> mapper) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        return myBatisProvider.mybatis(identity, mapper);
    }

    public static <T> void mybatisTx(String identity, Class<T> mapper, Consumer<T> consumer) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        myBatisProvider.mybatisTx(identity, mapper, consumer);
    }

    public static <T> void mybatisBatch(String identity, Class<T> mapper, Consumer<T> consumer) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        myBatisProvider.mybatisBatch(identity, mapper, consumer);
    }

    public static <T> T jpa(String identity, Class<T> repositoryClass) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        return jpaProvider.jpa(identity, repositoryClass);
    }

    public static <T> void jpaTx(String identity, Class<T> repositoryClass, Consumer<T> consumer) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaTx(identity, repositoryClass, consumer);
    }

    public static void jpaTxBegin(String identity) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaTxBegin(identity);
    }

    public static void jpaTxCommit(String identity) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaTxCommit(identity);
    }

    public static void jpaTxRollback(String identity) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaTxRollback(identity);
    }

    public static void jpaPrintCreateSchemaSQL(String identity) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaPrintCreateSchemaSQL(identity);
    }

    public static void jpaPrintUpdateSchemaSQL(String identity) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaPrintUpdateSchemaSQL(identity);
    }

    public static void jmsListener(String identity, DestinationType type, String destination, AckMode acknowledgeMode, MessageListener listener) {
        if (jmsProvider == null) throw new UnsupportedOperationException("Jms support not available");
        jmsProvider.consume(identity, destination, acknowledgeMode, type, listener);
    }

    public static void jmsProducer(String identity, Consumer<JMSContext> consumer) {
        if (jmsProvider == null) throw new UnsupportedOperationException("Jms support not available");
        jmsProvider.produce(identity, consumer);
    }

    public static <K, V> void kafkaProducer(String identity, Consumer<KafkaProducerController<K, V>> consumer) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.produce(identity, consumer);
    }

    public static <K, V> void kafkaConsumer(String identity, Consumer<KafkaConsumerController<K, V>> consumer) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.consumer(identity, consumer);
    }

    public static <K, V> void kafkaStream(String identity, Consumer<KafkaStreamController> consumer) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.stream(identity, consumer);
    }

    public static <T> ElasticRepository<T> elastic(String identity, Class<T> entityClass) {
        if (elasticProvider == null) throw new UnsupportedOperationException("ElasticSearch support not available");
        return elasticProvider.elastic(identity, entityClass);
    }

    public static <T> T httpClient(HttpClientConfig config, Class<T> clazz) {
        return new Kafir.KafirBuilder()
                .setBaseUri(config.getBaseUri())
                .setAuthenticator(config.getAuthenticator())
                .setInterceptor(config.getInterceptor())
                .setHeaders(config.getHeaders())
                .setConnectionTimeout(config.getConnectionTimeout())
                .setExecutorService(config.getExecutorService())
                .setVersion(config.getVersion())
                .setSslContext(config.getSslContext())
                .build(clazz);
    }
}
