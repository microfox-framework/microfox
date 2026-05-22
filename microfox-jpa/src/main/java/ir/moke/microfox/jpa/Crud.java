package ir.moke.microfox.jpa;

import ir.moke.microfox.api.jpa.TransactionPolicy;
import jakarta.persistence.TypedQuery;

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

    public static <T> List<T> select(String identity, String hql, Map<String, String> parameters, Class<T> entityClass, Integer offset, Integer size) {
        Objects.requireNonNull(identity, "JPA hql could not be null");
        Objects.requireNonNull(entityClass, "JPA entity class could not be null");

        List<T> result = new ArrayList<>();
        persistence(identity, TransactionPolicy.NOT_SUPPORTED, em -> {
            TypedQuery<T> typedQuery = em.createQuery(hql, entityClass);
            if (parameters != null && !parameters.isEmpty()) parameters.forEach(typedQuery::setParameter);
            Optional.ofNullable(offset).ifPresent(typedQuery::setFirstResult);
            Optional.ofNullable(size).ifPresent(typedQuery::setMaxResults);
            result.addAll(typedQuery.getResultList());
        });

        return result;
    }

    public static <T> Long count(String identity, String hql, Map<String, String> parameters) {
        Objects.requireNonNull(identity, "JPA hql could not be null");

        AtomicLong countRef = new AtomicLong();
        persistence(identity, TransactionPolicy.NOT_SUPPORTED, em -> {
            TypedQuery<Long> typedQuery = em.createQuery(hql, Long.class);
            if (parameters != null && !parameters.isEmpty()) parameters.forEach(typedQuery::setParameter);
            Long count = typedQuery.getSingleResult();
            countRef.set(count);
        });

        return countRef.get();
    }

    public static <T> List<T> select(String identity, String hql, Map<String, String> parameters, Class<T> entityClass) {
        Objects.requireNonNull(identity, "JPA hql could not be null");
        Objects.requireNonNull(entityClass, "JPA entity class could not be null");

        return select(identity, hql, parameters, entityClass, null, null);
    }

    public static <T> T select(String identity, Object primaryKey, Class<T> entityClass) {
        Objects.requireNonNull(identity, "JPA identity could not be null");
        Objects.requireNonNull(primaryKey, "JPA primary key could not be null");
        Objects.requireNonNull(entityClass, "JPA entity class could not be null");

        AtomicReference<T> ref = new AtomicReference<>();
        persistence(identity, TransactionPolicy.NOT_SUPPORTED, em -> ref.set(em.find(entityClass, primaryKey)));
        return ref.get();
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
}
