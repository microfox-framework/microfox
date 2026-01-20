package ir.moke.microfox.exception;

import ir.moke.microfox.api.http.ErrorObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ExceptionMapper<T extends Throwable> {
    private final Type type;

    protected ExceptionMapper() {
        // Extract generic type through reflection
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType paramType) {
            Type[] typeArgs = paramType.getActualTypeArguments();
            this.type = typeArgs[0];
        } else {
            throw new IllegalStateException("Cannot determine generic type");
        }
    }

    public Type getType() {
        return type;
    }

    public abstract ErrorObject toResponse(T throwable);
}
