package ir.moke.microfox;

import ir.moke.microfox.api.ftp.FtpFile;
import ir.moke.microfox.api.ftp.FtpProvider;
import ir.moke.microfox.api.ftp.MicroFoxFtpConfig;
import ir.moke.microfox.api.http.Filter;
import ir.moke.microfox.api.http.HttpProvider;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.job.JobProvider;
import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.mybatis.MyBatisProvider;
import ir.moke.microfox.api.redis.Redis;
import ir.moke.microfox.api.redis.RedisProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class MicroFox {
    private static final Logger logger = LoggerFactory.getLogger(MicroFox.class);
    private static final HttpProvider httpProvider = ServiceLoader.load(HttpProvider.class).findFirst().orElse(null);
    private static final JobProvider jobProvider = ServiceLoader.load(JobProvider.class).findFirst().orElse(null);
    private static final FtpProvider ftpProvider = ServiceLoader.load(FtpProvider.class).findFirst().orElse(null);
    private static final MyBatisProvider myBatisProvider = ServiceLoader.load(MyBatisProvider.class).findFirst().orElse(null);
    private static final JpaProvider jpaProvider = ServiceLoader.load(JpaProvider.class).findFirst().orElse(null);
    private static final RedisProvider redisProvider = ServiceLoader.load(RedisProvider.class).findFirst().orElse(null);

    static {
        MicroFoxConfig.introduce();
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

    public static <T> void mybatis(String identity, Class<T> mapper, Consumer<T> consumer) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        myBatisProvider.mybatis(identity, mapper, consumer);
    }

    public static <T> void mybatisTx(String identity, Class<T> mapper, Consumer<T> consumer) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        myBatisProvider.mybatisTx(identity, mapper, consumer);
    }

    public static <T> void mybatisBatch(String identity, Class<T> mapper, Consumer<T> consumer) {
        if (myBatisProvider == null) throw new UnsupportedOperationException("MyBatis support not available");
        myBatisProvider.mybatisBatch(identity, mapper, consumer);
    }

    public static <T> void jpa(Class<T> repositoryClass, String persistenceUnitName, Consumer<T> consumer) {
        if (jpaProvider == null) throw new UnsupportedOperationException("JPA support not available");
        jpaProvider.jpa(repositoryClass, persistenceUnitName, consumer);
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

    public static <T> void redis(String identity, Consumer<Redis> consumer) {
        if (redisProvider == null) throw new UnsupportedOperationException("Redis support not available");
        redisProvider.redis(identity, consumer);
    }
}
