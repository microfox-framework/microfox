package ir.moke.microfox.jpa;

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
        Crud.insert(identity, t);
    }

    public void update(T t) {
        Crud.update(identity, t);
    }

    public void remove(T t) {
        Crud.delete(identity, t);
    }

    public void removeByPrimaryKey(Object primaryKey) {
        Crud.delete(identity, primaryKey, entityClass);
    }

    public T findByPrimaryKey(Object primaryKey) {
        return Crud.select(identity, primaryKey, entityClass);
    }

    public List<T> find(String hql, Map<String, String> parameters, Integer offset, Integer size) {
        return Crud.select(identity, hql, parameters, entityClass, offset, size);
    }

    public List<T> find(String hql, Map<String, String> parameters) {
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

    public Long count(String hql, Map<String, String> parameters) {
        return Crud.count(identity, hql, parameters);
    }

    public boolean exists(String hql, Map<String, String> parameters) {
        return Crud.count(identity, hql, parameters) > 0;
    }
}
