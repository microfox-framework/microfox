package ir.moke.microfox;

import ir.moke.kafir.http.Kafir;
import ir.moke.microfox.http.Filter;
import ir.moke.microfox.http.Method;
import ir.moke.microfox.http.ResourceHolder;
import ir.moke.microfox.http.Route;
import ir.moke.microfox.job.JobSchedulerContainer;
import ir.moke.microfox.persistence.BatisExecutor;
import org.apache.ibatis.session.SqlSession;
import org.quartz.Job;

import java.net.http.HttpClient;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class MicroFox {
    public static void filter(String path, Filter... filters) {
        ResourceHolder.instance.addFilter(path, filters);
    }

    public static void get(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.GET, path, route);
    }

    public static void post(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.POST, path, route);
    }

    public static void delete(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.DELETE, path, route);
    }

    public static void put(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.PUT, path, route);
    }

    public static void patch(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.PATCH, path, route);
    }

    public static void head(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.HEAD, path, route);
    }

    public static void options(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.OPTIONS, path, route);
    }

    public static void trace(String path, Route route) {
        ResourceHolder.instance.addRoute(Method.TRACE, path, route);
    }

    public static <T> void restCall(String baseUri, Class<T> serviceClass, Consumer<T> consumer) {
        restCall(baseUri, Map.of(), serviceClass, consumer);
    }

    public static <T> void restCall(String baseUri, Map<String, String> headers, Class<T> serviceClass, Consumer<T> consumer) {
        T t = new Kafir.KafirBuilder()
                .setBaseUri(baseUri)
                .setVersion(HttpClient.Version.HTTP_2)
                .setHeaders(headers)
                .build(serviceClass);
        consumer.accept(t);
    }

    public static void job(Class<? extends Job> jobClass, String cronExpression) {
        JobSchedulerContainer.instance.register(jobClass, cronExpression);
    }

    public static void job(Class<? extends Job> jobClass, ZonedDateTime zonedDateTime) {
        JobSchedulerContainer.instance.register(jobClass, Date.from(zonedDateTime.toInstant()));
    }

    public static <T, R> R sql(Class<T> mapper, Function<T, R> function) {
        try (SqlSession sqlSession = BatisExecutor.getSqlSessionFactory().openSession(true)) {
            T t = sqlSession.getMapper(mapper);
            return function.apply(t);
        }
    }

    public static <T> void sqlBatch(Class<T> mapper, Consumer<T> consumer) {
        SqlSession batchSession = BatisExecutor.getBatchSession();
        T t = batchSession.getMapper(mapper);
        try {
            consumer.accept(t);
            batchSession.commit();
        } catch (Exception e) {
            batchSession.rollback();
            throw new RuntimeException("Failed to execute SQL batch", e);
        } finally {
            batchSession.close();
        }
    }
}
