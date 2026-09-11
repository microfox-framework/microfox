package ir.moke.microfox.jpa;

import ir.moke.microfox.api.jpa.TransactionPolicy;
import ir.moke.microfox.exception.MicroFoxException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Crud extends Jpa {
    public static <T> void insert(String identity, T t) {
        Objects.requireNonNull(identity, "JPA identity could not be null");
        Objects.requireNonNull(t, "JPA entity object could not be null");

        persistence(identity, TransactionPolicy.REQUIRED, em -> em.persist(t));
    }

    public static <T> void update(String identity, T t) {
        Objects.requireNonNull(identity, "JPA identity could not be null");
        Objects.requireNonNull(t, "JPA entity object could not be null");

        persistence(identity, TransactionPolicy.REQUIRED, em -> em.merge(t));
    }

    public static <T> void delete(String identity, Object entity) {
        Objects.requireNonNull(identity, "JPA identity could not be null");
        Objects.requireNonNull(entity, "JPA entity object could not be null");

        persistence(identity, TransactionPolicy.REQUIRED, em -> {
            Object managed = em.contains(entity) ? entity : em.merge(entity);
            em.remove(managed);
        });
    }

    public static <T> void delete(String identity, Object primaryKey, Class<T> entityClass) {
        Objects.requireNonNull(identity, "JPA identity could not be null");
        Objects.requireNonNull(primaryKey, "JPA primary key could not be null");
        Objects.requireNonNull(entityClass, "JPA entity class could not be null");

        T t = select(identity, primaryKey, entityClass);
        Optional.ofNullable(t).ifPresent(item -> delete(identity, t));
    }

    public static <T> void execute(String identity, String query, Map<String, Object> parameters, boolean isNative) {
        persistence(identity, TransactionPolicy.REQUIRED, em -> {
            Query q = isNative ? em.createNativeQuery(query) : em.createQuery(query);
            parameters.forEach(q::setParameter);
            q.executeUpdate();
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> select(String identity, String query, Map<String, Object> parameters, Class<T> entityClass, Integer offset, Integer size, boolean isNative) {
        Objects.requireNonNull(identity, "JPA query could not be null");
        Objects.requireNonNull(entityClass, "JPA entity class could not be null");

        List<T> result = new ArrayList<>();
        persistence(identity, TransactionPolicy.NOT_SUPPORTED, em -> {
            Query q = isNative ? em.createNativeQuery(query) : em.createQuery(query);
            if (parameters != null && !parameters.isEmpty()) parameters.forEach(q::setParameter);
            Optional.ofNullable(offset).ifPresent(q::setFirstResult);
            Optional.ofNullable(size).ifPresent(q::setMaxResults);
            result.addAll(q.getResultList());
        });

        return result;
    }

    public static <T> Long count(String identity, String query, Map<String, Object> parameters, boolean isNative) {
        Objects.requireNonNull(identity, "JPA query could not be null");

        if (!query.startsWith("select count")) throw new MicroFoxException("Count query must started with [select count...]");

        AtomicLong countRef = new AtomicLong();
        persistence(identity, TransactionPolicy.NOT_SUPPORTED, em -> {
            try {
                Query q = isNative ? em.createNativeQuery(query) : em.createQuery(query);
                if (parameters != null && !parameters.isEmpty()) parameters.forEach(q::setParameter);
                Long count = (Long) q.getSingleResult();
                countRef.set(count);
            } catch (NoResultException nre) {
                countRef.set(0);
            }
        });

        return countRef.get();
    }

    public static <T> List<T> select(String identity, String query, Map<String, Object> parameters, Class<T> entityClass, boolean isNative) {
        Objects.requireNonNull(identity, "JPA query could not be null");
        Objects.requireNonNull(entityClass, "JPA entity class could not be null");

        return select(identity, query, parameters, entityClass, null, null, isNative);
    }

    public static <T> T select(String identity, Object primaryKey, Class<T> entityClass) {
        Objects.requireNonNull(identity, "JPA identity could not be null");
        Objects.requireNonNull(primaryKey, "JPA primary key could not be null");
        Objects.requireNonNull(entityClass, "JPA entity class could not be null");

        AtomicReference<T> ref = new AtomicReference<>();
        persistence(identity, TransactionPolicy.NOT_SUPPORTED, em -> ref.set(em.find(entityClass, primaryKey)));
        return ref.get();
    }
}
