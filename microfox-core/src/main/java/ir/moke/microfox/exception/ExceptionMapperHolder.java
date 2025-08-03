package ir.moke.microfox.exception;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionMapperHolder {
    private static final Map<Type, ExceptionMapper<? extends Throwable>> MAPPERS = new ConcurrentHashMap<>();

    public static <T extends Throwable> void add(ExceptionMapper<T> exceptionMapper) {
        for (Type type : exceptionMapper.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType parameterizedType) {
                Type rawType = parameterizedType.getRawType();
                if (rawType instanceof Class<?> rawClass && ExceptionMapper.class.isAssignableFrom(rawClass)) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    if (actualType instanceof Class<?> exceptionType) {
                        System.out.println("Generic Exception Type: " + exceptionType.getName());
                        // You can safely cast and store the mapper
                        MAPPERS.put(actualType, exceptionMapper);
                    }
                }
            }
        }
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
