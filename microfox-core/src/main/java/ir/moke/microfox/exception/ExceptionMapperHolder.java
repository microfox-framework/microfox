package ir.moke.microfox.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionMapperHolder {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionMapperHolder.class);
    private static final Map<Type, ExceptionMapper<? extends Throwable>> MAPPERS = new ConcurrentHashMap<>();

    public static <T extends Throwable> void add(ExceptionMapper<T> exceptionMapper) {
        Type type = exceptionMapper.getType();

        // ignore infrastructure mappers
        if (!MAPPERS.containsKey(type)) MAPPERS.put(type, exceptionMapper);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> ExceptionMapper<T> get(Throwable throwable) {
        Class<?> exceptionClass = throwable.getClass();

        // Try exact match first
        ExceptionMapper<? extends Throwable> mapper = MAPPERS.get(exceptionClass);
        if (mapper != null) return (ExceptionMapper<T>) mapper;

        // If not found, check for assignable superclasses/interfaces
        for (Map.Entry<Type, ExceptionMapper<? extends Throwable>> entry : MAPPERS.entrySet()) {
            if (entry.getKey() instanceof Class<?> typeClass && typeClass.isAssignableFrom(exceptionClass)) {
                return (ExceptionMapper<T>) entry.getValue();
            }
        }

        // Not found
        return null;
    }
}
