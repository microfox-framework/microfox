package ir.moke.microfox.db.jpa;

import ir.moke.microfox.db.jpa.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RepositoryHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryHandler.class);
    private final EntityManager em;

    public RepositoryHandler(String persistenceUnitName) {
        this.em = MicroFoxJpa.getEntityManager(persistenceUnitName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        logger.debug("Called: {}, args: {}", name, Arrays.toString(args));

        if (method.isAnnotationPresent(NamedQuery.class)) {
            return invokeNamedQuery(em, method, args);
        } else if (method.isAnnotationPresent(Query.class)) {
            return invokeQueryString(em, method, args);
        } else if (method.isAnnotationPresent(Find.class)) {
            return findByPrimaryKey(em, method, args);
        } else if (method.isAnnotationPresent(Merge.class)) {
            return merge(em, method, args);
        } else if (method.isAnnotationPresent(Remove.class)) {
            return remove(em, method, args);
        } else if (method.isAnnotationPresent(Persist.class)) {
            return persist(em, method, args);
        } else if (method.isAnnotationPresent(NamedStoredProcedure.class)) {
            return invokeNamedStoredProcedure(em, method, args);
        } else if (method.isAnnotationPresent(StoredProcedure.class)) {
            return invokeStoredProcedure(em, method, args);
        }

        throw new AbstractMethodError("No handler logic for method: " + method);
    }

    public static Object persist(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Class<?> entityClass = method.getParameterTypes()[0];
        final Object entity = args[0];

        if (entity == null) {
            throw new ValidationException(entityClass.getSimpleName() + " object is null");
        }
        em.persist(entity);
        if (isVoid(method.getReturnType())) {
            return null;
        } else {
            return entity;
        }
    }

    public static Object findByPrimaryKey(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Class<?> entityClass = method.getReturnType();
        final Object primaryKey = args[0];

        if (primaryKey == null) {
            throw new ValidationException("Invalid id");
        }
        return em.find(entityClass, primaryKey);
    }

    public static Object invokeNamedQuery(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final NamedQuery namedQuery = method.getAnnotation(NamedQuery.class);
        final jakarta.persistence.Query query = em.createNamedQuery(namedQuery.value());
        if (namedQuery.update()) {
            return update(method, args, query);
        } else {
            return select(method, args, query);
        }
    }

    public static Object invokeQueryString(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Query queryString = method.getAnnotation(Query.class);
        final jakarta.persistence.Query query = em.createQuery(queryString.value());

        if (queryString.update()) {
            return update(method, args, query);
        } else {
            return select(method, args, query);
        }
    }

    private static Object select(Method method, Object[] args, jakarta.persistence.Query query) {
        Integer offset = null;
        Integer maxResults = null;

        for (final Parameter parameter : params(method, args)) {
            final QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
            if (queryParam != null) {
                if (parameter.getValue() == null) {
                    throw new ValidationException(queryParam.value() + " is null");
                }

                query.setParameter(queryParam.value(), parameter.getValue());
            }

            final Offset o = parameter.getAnnotation(Offset.class);
            if (o != null && (isInt(parameter.getType()))) {
                offset = (Integer) parameter.getValue();
            }

            final MaxResults m = parameter.getAnnotation(MaxResults.class);
            if (m != null && (isInt(parameter.getType()))) {
                maxResults = (Integer) parameter.getValue();
            }
        }

        if (offset != null && maxResults != null) {
            query.setFirstResult(offset);
            query.setMaxResults(maxResults);
        }

        try {
            return (isList(method)) ? query.getResultList() : query.getSingleResult();
        } catch (final NoResultException e) {
            return null;
        }
    }

    private static Object update(Method method, Object[] args, jakarta.persistence.Query query) {
        for (final Parameter parameter : params(method, args)) {
            final QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
            if (queryParam != null) {
                if (parameter.getValue() == null) {
                    throw new ValidationException(queryParam.value() + " is null");
                }
                query.setParameter(queryParam.value(), parameter.getValue());
            }
        }
        if (isInt(method.getReturnType())) {
            return query.executeUpdate();
        } else if (isVoid(method.getReturnType())) {
            query.executeUpdate();
            return null;
        } else {
            throw new IllegalArgumentException("Update methods must have a void or int return type");
        }
    }

    public static Object merge(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Class<?> entityClass = method.getParameterTypes()[0];
        final Object entity = args[0];

        if (entity == null) {
            throw new ValidationException(entityClass.getSimpleName() + " object is null");
        }

        return em.merge(entity);
    }

    public static Object remove(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Class<?> entityClass = method.getParameterTypes()[0];
        final Object entity = args[0];

        if (entity == null) {
            throw new ValidationException(entityClass.getSimpleName() + " object is null");
        }
        em.remove(em.merge(entity));
        return null;
    }

    private static Object invokeNamedStoredProcedure(EntityManager em, Method method, Object[] args) {
        NamedStoredProcedure ann = method.getAnnotation(NamedStoredProcedure.class);
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(ann.value());
        setParameters(query, method, args);
        return query.getResultList();
    }

    private static Object invokeStoredProcedure(EntityManager em, Method method, Object[] args) {
        StoredProcedure ann = method.getAnnotation(StoredProcedure.class);
        StoredProcedureQuery query = em.createStoredProcedureQuery(ann.procedureName());
        setParameters(query, method, args);
        return query.getResultList();
    }

    private static void setParameters(StoredProcedureQuery query, Method method, Object[] args) {
        List<Parameter> params = params(method, args);
        for (int i = 0; i < params.size(); i++) {
            QueryParam param = params.get(i).getAnnotation(QueryParam.class);
            if (param != null) {
                query.setParameter(param.value(), args[i]);
            }
        }
    }

    private static boolean isList(final Method method) {
        return Collection.class.isAssignableFrom(method.getReturnType());
    }

    private static boolean isInt(final Class<?> clazz) {
        return Integer.class.isAssignableFrom(clazz) || clazz == int.class;
    }

    private static boolean isVoid(final Class<?> clazz) {
        return void.class.equals(clazz) || Void.class.equals(clazz);
    }

    public static List<Parameter> params(Method method, Object[] values) {
        Annotation[][] annotations = method.getParameterAnnotations();
        Class<?>[] types = method.getParameterTypes();

        return IntStream.range(0, method.getParameterCount())
                .mapToObj(i -> new Parameter(annotations[i], types[i], values[i]))
                .collect(Collectors.toList());
    }
}
