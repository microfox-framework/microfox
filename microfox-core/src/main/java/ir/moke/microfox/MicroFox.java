package ir.moke.microfox;

import ir.moke.microfox.api.ftp.FtpFile;
import ir.moke.microfox.api.ftp.FtpProvider;
import ir.moke.microfox.api.ftp.MicroFoxFtpConfig;
import ir.moke.microfox.api.http.Filter;
import ir.moke.microfox.api.http.HttpProvider;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.sse.SseObject;
import ir.moke.microfox.api.jms.DestinationType;
import ir.moke.microfox.api.jms.JmsProvider;
import ir.moke.microfox.api.job.JobProvider;
import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.kafka.KafkaConsumerController;
import ir.moke.microfox.api.kafka.KafkaProducerController;
import ir.moke.microfox.api.kafka.KafkaProvider;
import ir.moke.microfox.api.mybatis.MyBatisProvider;
import jakarta.jms.MessageListener;
import jakarta.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MicroFox {
    private static final Logger logger = LoggerFactory.getLogger(MicroFox.class);
    private static final HttpProvider httpProvider = ServiceLoader.load(HttpProvider.class).findFirst().orElse(null);
    private static final JobProvider jobProvider = ServiceLoader.load(JobProvider.class).findFirst().orElse(null);
    private static final FtpProvider ftpProvider = ServiceLoader.load(FtpProvider.class).findFirst().orElse(null);
    private static final MyBatisProvider myBatisProvider = ServiceLoader.load(MyBatisProvider.class).findFirst().orElse(null);
    private static final JpaProvider jpaProvider = ServiceLoader.load(JpaProvider.class).findFirst().orElse(null);
    private static final JmsProvider jmsProvider = ServiceLoader.load(JmsProvider.class).findFirst().orElse(null);
    private static final KafkaProvider kafkaProvider = ServiceLoader.load(KafkaProvider.class).findFirst().orElse(null);

    static {
        MicrofoxEnvironment.introduce();
    }

    public static void httpFilter(String path, Filter... filters) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.filter(path, filters);
    }

    public static void httpGet(String path, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.get(path, route);
    }

    public static void httpPost(String path, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.post(path, route);
    }

    public static void httpDelete(String path, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.delete(path, route);
    }

    public static void httpPut(String path, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.put(path, route);
    }

    public static void httpPatch(String path, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.patch(path, route);
    }

    public static void httpHead(String path, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.head(path, route);
    }

    public static void httpOptions(String path, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.options(path, route);
    }

    public static void httpTrace(String path, Route route) {
        if (httpProvider == null) throw new UnsupportedOperationException("HTTP support not available");
        httpProvider.trace(path, route);
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

    public static <T, R> R mybatis(String identity, Class<T> mapper, Function<T, R> function) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        return myBatisProvider.mybatis(identity, mapper, function);
    }

    public static <T> void mybatisTx(String identity, Class<T> mapper, Consumer<T> consumer) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        myBatisProvider.mybatisTx(identity, mapper, consumer);
    }

    public static <T> void mybatisBatch(String identity, Class<T> mapper, Consumer<T> consumer) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        myBatisProvider.mybatisBatch(identity, mapper, consumer);
    }

    public static <T, R> R jpa(Class<T> repositoryClass, String persistenceUnitName, Function<T, R> function) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        return jpaProvider.jpa(repositoryClass, persistenceUnitName, function);
    }

    public static <T> void jpaTx(Class<T> repositoryClass, String persistenceUnitName, Consumer<T> consumer) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaTx(repositoryClass, persistenceUnitName, consumer);
    }

    public static void jpaTxBegin(String persistenceUnitName) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaTxBegin(persistenceUnitName);
    }

    public static void jpaTxCommit(String persistenceUnitName) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaTxCommit(persistenceUnitName);
    }

    public static void jpaTxRollback(String persistenceUnitName) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaTxRollback(persistenceUnitName);
    }

    public static void jpaPrintCreateSchemaSQL(String persistenceUnitName) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaPrintCreateSchemaSQL(persistenceUnitName);
    }

    public static void jpaPrintGenerateUpdateSchemaSQL(String persistenceUnitName) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpaPrintUpdateSchemaSQL(persistenceUnitName);
    }

    public static void jmsListener(String identity, String destination, DestinationType type, int acknowledgeMode, MessageListener listener) {
        if (jmsProvider == null) throw new UnsupportedOperationException("Jms support not available");
        if (type.equals(DestinationType.QUEUE)) {
            jmsProvider.consumeQueue(identity, destination, acknowledgeMode, listener);
        } else {
            jmsProvider.consumeTopic(identity, destination, acknowledgeMode, listener);
        }
    }

    public static void jmsProducer(String identity, boolean transacted, int acknowledgeMode, DestinationType type, Consumer<Session> consumer) {
        if (jmsProvider == null) throw new UnsupportedOperationException("Jms support not available");
        if (type.equals(DestinationType.QUEUE)) {
            jmsProvider.produceQueue(identity, transacted, acknowledgeMode, consumer);
        } else {
            jmsProvider.produceTopic(identity, transacted, acknowledgeMode, consumer);
        }
    }

    public static <K, V> void kafkaProducer(String identity, Consumer<KafkaProducerController<K, V>> consumer) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.produce(identity, consumer);
    }

    public static <K, V> void kafkaConsumer(String identity, Consumer<KafkaConsumerController<K, V>> consumer) {
        if (kafkaProvider == null) throw new UnsupportedOperationException("Kafka support not available");
        kafkaProvider.consumer(identity, consumer);
    }
}
