package ir.moke.microfox.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionMapperHolder {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionMapperHolder.class);
    private static final Map<Class<? extends Throwable>, ExceptionMapper> MAPPERS = new ConcurrentHashMap<>();

    public static <T extends Throwable> void add(Class<T> t, ExceptionMapper mapper) {
        MAPPERS.put(t, mapper);
    }

    public static <T extends Throwable> void remove(Class<T> t) {
        MAPPERS.remove(t);
    }

    public static <T extends Throwable> ExceptionMapper get(Class<T> t) {
        return MAPPERS.get(t);
    }
}
