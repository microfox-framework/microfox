package ir.moke.microfox.jpa;

import ir.moke.microfox.exception.MicroFoxException;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OptionalRepository<T> {
    private final String identity;
    private final Class<T> entityClass;

    private OptionalRepository(String identity, Class<T> entityClass) {
        this.identity = identity;
        this.entityClass = entityClass;
    }

    public static <T> OptionalRepository<T> of(String identity, Class<T> entityClass) {
        return new OptionalRepository<T>(identity, entityClass);
    }

    public void save(T t) {
        boolean isIdNull = isPrimaryKeyIsNull(t);
        if (isIdNull) {
            Crud.insert(identity, t);
        } else {
            Crud.update(identity, t);
        }
    }

    public void remove(T t) {
        Crud.delete(identity, t);
    }

    public T find(Object primaryKey) {
        return Crud.select(identity, primaryKey, entityClass);
    }

    public List<T> find(String hql, Map<String, Object> parameters, Integer offset, Integer size) {
        return Crud.select(identity, hql, parameters, entityClass, offset, size);
    }

    public List<T> find(String hql, Map<String, Object> parameters) {
        return Crud.select(identity, hql, parameters, entityClass);
    }

    public List<T> find() {
        String entityName = entityClass.getSimpleName();
        return Crud.select(identity, "from %s".formatted(entityName), null, entityClass, null, null);
    }

    public Long count() {
        String entityName = entityClass.getSimpleName();
        return Crud.count(identity, "select count(1) from %s".formatted(entityName), null);
    }

    public Long count(String hql, Map<String, Object> parameters) {
        return Crud.count(identity, hql, parameters);
    }

    public boolean exists(String hql, Map<String, Object> parameters) {
        return Crud.count(identity, hql, parameters) > 0;
    }

    /*-------------------------------*/

    private static <T> boolean isPrimaryKeyIsNull(T t) {
        boolean isPrimaryKeyNull = false;
        try {
            Field idField = Arrays.stream(t.getClass().getFields()).filter(item -> item.isAnnotationPresent(Id.class)).findFirst().orElse(null);
            if (idField != null) {
                idField.setAccessible(true);
                isPrimaryKeyNull = idField.get(t) == null;

                idField.setAccessible(false);
            } else {
                Method idMethod = Arrays.stream(t.getClass().getMethods()).filter(item -> item.isAnnotationPresent(Id.class)).findFirst().orElse(null);
                if (idMethod != null) {
                    idMethod.setAccessible(true);
                    isPrimaryKeyNull = idMethod.invoke(t) != null;
                    idMethod.setAccessible(false);
                }
            }
            return isPrimaryKeyNull;
        } catch (Exception e) {
            throw new MicroFoxException(e);
        }
    }
}
