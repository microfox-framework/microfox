package ir.moke.microfox;

import com.jcraft.jsch.ChannelSftp;
import com.mongodb.client.MongoCollection;
import ir.moke.kafir.http.Kafir;
import ir.moke.microfox.api.elastic.ElasticProvider;
import ir.moke.microfox.api.elastic.ElasticRepository;
import ir.moke.microfox.api.ftp.*;
import ir.moke.microfox.api.groovy.GroovyProvider;
import ir.moke.microfox.api.hc.HealthCheckProvider;
import ir.moke.microfox.api.http.*;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.api.jms.AckMode;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsProvider;
import ir.moke.microfox.api.job.JobInfo;
import ir.moke.microfox.api.job.JobProvider;
import ir.moke.microfox.api.job.Task;
import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.api.kafka.KafkaProvider;
import ir.moke.microfox.api.kafka.KafkaStreamController;
import ir.moke.microfox.api.metrics.MetricsProvider;
import ir.moke.microfox.api.mongodb.MongoProvider;
import ir.moke.microfox.api.mybatis.MyBatisProvider;
import ir.moke.microfox.api.openapi.OpenApiProvider;
import ir.moke.microfox.api.redis.RedisProvider;
import ir.moke.microfox.api.system.SystemProvider;
import ir.moke.microfox.logger.LoggerManager;
import ir.moke.microfox.logger.model.LogModel;
import ir.moke.microfox.utils.HttpClientConfig;
import jakarta.jms.JMSContext;
import jakarta.jms.MessageListener;
import jakarta.persistence.EntityManager;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;

public class MicroFox {

    private static final HttpProvider httpProvider = ServiceLoader.load(HttpProvider.class).findFirst().orElse(null);
    private static final JobProvider jobProvider = ServiceLoader.load(JobProvider.class).findFirst().orElse(null);
    private static final FtpProvider ftpProvider = ServiceLoader.load(FtpProvider.class).findFirst().orElse(null);
    private static final SftpProvider sftpProvider = ServiceLoader.load(SftpProvider.class).findFirst().orElse(null);
    private static final MyBatisProvider myBatisProvider = ServiceLoader.load(MyBatisProvider.class).findFirst().orElse(null);
    private static final JpaProvider jpaProvider = ServiceLoader.load(JpaProvider.class).findFirst().orElse(null);
    private static final JmsProvider jmsProvider = ServiceLoader.load(JmsProvider.class).findFirst().orElse(null);
    private static final KafkaProvider kafkaProvider = ServiceLoader.load(KafkaProvider.class).findFirst().orElse(null);
    private static final ElasticProvider elasticProvider = ServiceLoader.load(ElasticProvider.class).findFirst().orElse(null);
    private static final OpenApiProvider openApiProvider = ServiceLoader.load(OpenApiProvider.class).findFirst().orElse(null);
    private static final MetricsProvider metricsProvider = ServiceLoader.load(MetricsProvider.class).findFirst().orElse(null);
    private static final HealthCheckProvider healthCheckProvider = ServiceLoader.load(HealthCheckProvider.class).findFirst().orElse(null);
    private static final SystemProvider systemProvider = ServiceLoader.load(SystemProvider.class).findFirst().orElse(null);
    private static final MongoProvider mongoProvider = ServiceLoader.load(MongoProvider.class).findFirst().orElse(null);
    private static final GroovyProvider groovyProvider = ServiceLoader.load(GroovyProvider.class).findFirst().orElse(null);
    private static final RedisProvider redisProvider = ServiceLoader.load(RedisProvider.class).findFirst().orElse(null);

    static {
        MicroFoxEnvironment.introduce();
        Optional.ofNullable(systemProvider).ifPresent(SystemProvider::activate);
        Optional.ofNullable(healthCheckProvider).ifPresent(HealthCheckProvider::activate);
        Optional.ofNullable(openApiProvider).ifPresent(OpenApiProvider::registerOpenAPI);
    }

    public static void logger(LogModel log) {
        LoggerManager.registerLog(log);
    }

    public static void cors(Map<CORSHeader, String> valueMap) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.filter("/*", -801, (request, response, chain) -> {
            valueMap.forEach((k, v) -> response.header(k.getValue(), v));
            chain.doFilter(request, response);
        });
    }

    public static void corsAccessAll() {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.filter("/*", -800, "CORS", "microfox", (request, response, chain) -> {
            response.header(CORSHeader.ACCESS_CONTROL_ALLOW_ORIGIN.getValue(), "*");
            response.header(CORSHeader.ACCESS_CONTROL_ALLOW_METHODS.getValue(), "POST,GET,PUT,DELETE,OPTIONS");
            response.header(CORSHeader.ACCESS_CONTROL_ALLOW_HEADERS.getValue(), "Accept, Content-Type, Authorization,accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
            response.header(CORSHeader.ACCESS_CONTROL_ALLOW_CREDENTIALS.getValue(), "true");
            response.header(CORSHeader.ACCESS_CONTROL_MAX_AGE.getValue(), "86400");
            chain.doFilter(request, response);
        });
    }

    public static void filter(String path, int order, Filter filter) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.filter(path, order, filter);
    }

    public static void filter(String path, int order, String name, String category, Filter filter) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.filter(path, order, name, category, filter);
    }

    public static void filter(FilterInfo filterInfo) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.filter(filterInfo);
    }

    public static List<FilterInfo> filters() {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        return httpProvider.filterList();
    }

    public static void filterSort(String... sortedHash) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.filterSort(sortedHash);
    }

    public static void route(String path, HttpMethod httpMethod, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.route(path, httpMethod, route);
    }

    public static void route(String path, HttpMethod httpMethod, String name, String category, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.route(path, httpMethod, name, category, route);
    }

    public static void route(String path, HttpMethod httpMethod, Route route, SecurityStrategy strategy) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.route(path, httpMethod, route, strategy, List.of(), List.of());
    }

    public static void route(String path, HttpMethod httpMethod, Route route, SecurityStrategy strategy, List<String> roles, List<String> scopes) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.route(path, httpMethod, route, strategy, roles, scopes);
    }

    public static void route(RouteInfo routeInfo) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.route(routeInfo);
    }

    public static Set<RouteInfo> routes() {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        return httpProvider.routeList();
    }

    public static void websocket(Class<?> endpointClass) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.websocket(endpointClass);
    }

    public static void ssePublisher(String identity, SseObject sseObject) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.ssePublisher(identity, sseObject);
    }

    public static void job(Task task, String name, String cronExpression, boolean concurrentExecution) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.job(task, name, null, cronExpression, concurrentExecution);
    }

    public static void job(Task task, String name, Date date) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.job(task, name, null, date);
    }

    public static void job(Task task, String name, String group, String cronExpression, boolean concurrentExecution) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.job(task, name, group, cronExpression, concurrentExecution);
    }

    public static void job(Task task, String name, String group, Date date) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.job(task, name, group, date);
    }

    public static void jobPause(String name, String group) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.pauseJob(name, group);
    }

    public static void jobPause(String name) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.pauseJob(name, null);
    }

    public static void jobResume(String name, String group) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.resumeJob(name, group);
    }

    public static void jobResume(String name) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.resumeJob(name, null);
    }

    public static void jobResumeAll() {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.resumeAllJob();
    }

    public static void jobPauseAll() {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.pauseAllJob();
    }

    public static void jobDelete(String name, String group) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.deleteJob(name, group);
    }

    public static void jobDelete(String name) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        jobProvider.deleteJob(name, null);
    }

    public static List<JobInfo> jobs() {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        return jobProvider.listJobs();
    }

    public static JobInfo job(String name, String group) {
        if (jobProvider == null) throw new UnsupportedOperationException("Job scheduler support not available");
        return jobProvider.job(name, group);
    }

    public static void ftpDownload(MicroFoxFtpConfig config, Path remoteFilePath, Path localDownloadDir) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpDownload(config, remoteFilePath, localDownloadDir);
    }

    public static void ftpBatchDownload(MicroFoxFtpConfig config, List<Path> remoteFilePath, Path localDownloadDir) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpBatchDownload(config, remoteFilePath, localDownloadDir);
    }

    public static void ftpUpload(MicroFoxFtpConfig config, Path remoteFilePath, Path file) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpUpload(config, remoteFilePath, file);
    }

    public static void ftpBatchUpload(MicroFoxFtpConfig config, Path remoteFilePath, List<Path> files) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpBatchUpload(config, remoteFilePath, files);
    }

    public static void ftpDelete(MicroFoxFtpConfig config, Path remoteFilePath) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpDelete(config, remoteFilePath);
    }

    public static void ftpList(MicroFoxFtpConfig config, Path remoteFilePath, Consumer<FTPFile[]> consumer) {
        if (ftpProvider == null) throw new UnsupportedOperationException("FTP support not available");
        ftpProvider.ftpList(config, remoteFilePath, consumer);
    }

    public static void sftpDownload(MicroFoxSftpConfig config, Path remoteFilePath, Path localDownloadDir) {
        if (sftpProvider == null) throw new UnsupportedOperationException("SFTP support not available");
        sftpProvider.sftpDownload(config, remoteFilePath, localDownloadDir);
    }

    public static void sftpBatchDownload(MicroFoxSftpConfig config, List<Path> remoteFilePath, Path localDownloadDir) {
        if (sftpProvider == null) throw new UnsupportedOperationException("SFTP support not available");
        sftpProvider.sftpBatchDownload(config, remoteFilePath, localDownloadDir);
    }

    public static void sftpUpload(MicroFoxSftpConfig config, Path remoteDirPath, Path file, SftpMode mode) {
        if (sftpProvider == null) throw new UnsupportedOperationException("SFTP support not available");
        sftpProvider.sftpUpload(config, remoteDirPath, file, mode);
    }

    public static void sftpBatchUpload(MicroFoxSftpConfig config, Path remoteDirPath, List<Path> files, SftpMode mode) {
        if (sftpProvider == null) throw new UnsupportedOperationException("SFTP support not available");
        sftpProvider.sftpBatchUpload(config, remoteDirPath, files, mode);
    }

    public static void sftpDelete(MicroFoxSftpConfig config, Path remoteFilePath) {
        if (sftpProvider == null) throw new UnsupportedOperationException("SFTP support not available");
        sftpProvider.sftpDelete(config, remoteFilePath);
    }

    public static void sftpList(MicroFoxSftpConfig config, Path remoteFilePath, Consumer<Vector<ChannelSftp.LsEntry>> consumer) {
        if (sftpProvider == null) throw new UnsupportedOperationException("SFTP support not available");
        sftpProvider.sftpList(config, remoteFilePath, consumer);
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

    public static void jpa(String identity, Runnable runnable) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpa(identity, TransactionPolicy.REQUIRED, runnable);
    }

    public static void jpa(String identity, TransactionPolicy policy, Runnable runnable) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpa(identity, policy, runnable);
    }

    public static void jpa(String identity, Consumer<EntityManager> consumer) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpa(identity, TransactionPolicy.REQUIRED, consumer);
    }

    public static void jpa(String identity, TransactionPolicy policy, Consumer<EntityManager> consumer) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpa(identity, policy, consumer);
    }

    public static <T> void jpaTxRollback(String identity) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.rollback(identity);
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

    public static void jmsStop(String identity) {
        if (jmsProvider == null) throw new UnsupportedOperationException("Jms support not available");
        jmsProvider.stop(identity);
    }

    public static <K, V> void kafkaProducer(String clientId, Consumer<KafkaProducerController<K, V>> consumer) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.produce(clientId, consumer);
    }

    public static <K, V> void kafkaConsumer(String clientId, Consumer<KafkaConsumerController<K, V>> consumer) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.consumer(clientId, consumer);
    }

    public static <K, V> void kafkaStream(String clientId, Object topology, Consumer<KafkaStreamController> consumer) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.stream(clientId, topology, consumer);
    }

    public static <T> ElasticRepository<T> elastic(String identity, Class<T> entityClass) {
        if (elasticProvider == null) throw new UnsupportedOperationException("ElasticSearch support not available");
        return elasticProvider.elastic(identity, entityClass);
    }

    public static <T> T httpClient(HttpClientConfig config, Class<T> clazz) {
        return new Kafir.KafirBuilder().setBaseUri(config.getBaseUri()).setAuthenticator(config.getAuthenticator()).setInterceptor(config.getInterceptor()).setHeaders(config.getHeaders()).setConnectionTimeout(config.getConnectionTimeout()).setExecutorService(config.getExecutorService()).setVersion(config.getVersion()).setSslContext(config.getSslContext()).build(clazz);
    }

    public static <T> MongoCollection<T> mongo(String identity, Class<T> entityClass) {
        if (mongoProvider == null) throw new UnsupportedOperationException("MongoDB support not available");
        return mongoProvider.collection(identity, entityClass);
    }

    public static void groovyEval(String script, Consumer<Object> result) {
        if (groovyProvider == null) throw new UnsupportedOperationException("Groovy support not available");
        groovyProvider.eval(script, result);
    }

    public static void groovyEval(File file, Consumer<Object> result) {
        if (groovyProvider == null) throw new UnsupportedOperationException("Groovy support not available");
        groovyProvider.eval(file, result);
    }

    public static void groovyParse(String script, Consumer<Class<?>> classConsumer) {
        if (groovyProvider == null) throw new UnsupportedOperationException("Groovy support not available");
        groovyProvider.parse(script, classConsumer);
    }

    public static void groovyParse(File file, Consumer<Class<?>> classConsumer) {
        if (groovyProvider == null) throw new UnsupportedOperationException("Groovy support not available");
        groovyProvider.parse(file, classConsumer);
    }

    public static void groovyParse(String script, ClassLoader parentClassLoader, Consumer<Class<?>> classConsumer) {
        if (groovyProvider == null) throw new UnsupportedOperationException("Groovy support not available");
        groovyProvider.parse(script, parentClassLoader, classConsumer);
    }

    public static void groovyParse(File file, ClassLoader parentClassLoader, Consumer<Class<?>> classConsumer) {
        if (groovyProvider == null) throw new UnsupportedOperationException("Groovy support not available");
        groovyProvider.parse(file, parentClassLoader, classConsumer);
    }

    public static void metricGauge(String name, double value) {
        if (metricsProvider == null) throw new UnsupportedOperationException("Metrics support not available");
        metricsProvider.gauge(name, value);
    }

    public static void metricGauge(String name, Map<String, String> tags, double value) {
        if (metricsProvider == null) throw new UnsupportedOperationException("Metrics support not available");
        metricsProvider.gauge(name, tags, value);
    }

    public static void metricTimer(String name, Map<String, String> tags, Runnable runnable) {
        if (metricsProvider == null) throw new UnsupportedOperationException("Metrics support not available");
        metricsProvider.timer(name, tags, runnable);
    }

    public static void metricCounter(String name, Map<String, String> tags) {
        if (metricsProvider == null) throw new UnsupportedOperationException("Metrics support not available");
        metricsProvider.counter(name, tags);
    }

    public static Object redis(String identity) {
        if (redisProvider == null) throw new UnsupportedOperationException("redis support not available");
        return redisProvider.unwrap(identity);
    }
}
