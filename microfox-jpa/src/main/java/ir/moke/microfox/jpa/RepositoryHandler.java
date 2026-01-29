package ir.moke.microfox.jpa;

import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.jpa.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RepositoryHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryHandler.class);
    private final EntityManager em;

    public RepositoryHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        logger.trace("Called: {}, args: {}", name, Arrays.toString(args));

        if (name.equals("toString") && method.getParameterCount() == 0)
            return proxy.getClass().getName() + "@" + System.identityHashCode(proxy);
        if (name.equals("hashCode") && method.getParameterCount() == 0)
            return System.identityHashCode(proxy);
        if (name.equals("equals") && method.getParameterCount() == 1)
            return proxy == args[0];

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
        } else if (method.isAnnotationPresent(Criteria.class)) {
            return invokeCriteria(em, method, args);
        }

        throw new AbstractMethodError("No handler logic for method: " + method);
    }

    public static Object persist(final EntityManager em, final Method method, final Object[] args) {
        final Class<?> entityClass = method.getParameterTypes()[0];
        final Object entity = args[0];

        if (entity == null) {
            throw new MicroFoxException(entityClass.getSimpleName() + " object is null");
        }
        em.persist(entity);
        if (isVoid(method.getReturnType())) {
            return null;
        } else {
            return entity;
        }
    }

    public static Object findByPrimaryKey(final EntityManager em, final Method method, final Object[] args) {
        final Class<?> entityClass = method.getReturnType();
        final Object primaryKey = args[0];

        if (primaryKey == null) {
            throw new MicroFoxException("Invalid id");
        }
        return em.find(entityClass, primaryKey);
    }

    public static Object invokeNamedQuery(final EntityManager em, final Method method, final Object[] args) {
        final NamedQuery namedQuery = method.getAnnotation(NamedQuery.class);
        String value = namedQuery.value();
        final jakarta.persistence.Query query = em.createNamedQuery(value);
        if (value.toUpperCase().startsWith("UPDATE")) {
            return update(method, args, query);
        } else {
            return select(method, args, query);
        }
    }

    public static Object invokeQueryString(final EntityManager em, final Method method, final Object[] args) {
        final Query queryString = method.getAnnotation(Query.class);
        String value = queryString.value();
        boolean isNative = queryString.isNative();

        jakarta.persistence.Query query;
        if (isNative) {
            query = em.createNativeQuery(value);
        } else {
            query = em.createQuery(value);
        }

        if (value.toUpperCase().startsWith("UPDATE")) {
            return update(method, args, query);
        } else {
            return select(method, args, query);
        }
    }

    private static Object select(Method method, Object[] args, jakarta.persistence.Query query) {
        Integer offset = null;
        Integer maxResults = null;

        for (final Parameter parameter : params(method, args)) {
            final QueryParameter queryParameter = parameter.getAnnotation(QueryParameter.class);
            if (queryParameter != null) {
                query.setParameter(queryParameter.value(), parameter.getValue());
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
            return (isList(method)) ? controlSelectReturnList(method, query.getResultList()) : query.getSingleResult();
        } catch (final NoResultException e) {
            return null;
        }
    }

    private static Object update(Method method, Object[] args, jakarta.persistence.Query query) {
        for (final Parameter parameter : params(method, args)) {
            final QueryParameter queryParameter = parameter.getAnnotation(QueryParameter.class);
            if (queryParameter != null) {
                query.setParameter(queryParameter.value(), parameter.getValue());
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

    public static Object merge(final EntityManager em, final Method method, final Object[] args) {
        final Class<?> entityClass = method.getParameterTypes()[0];
        final Object entity = args[0];

        if (entity == null) {
            throw new MicroFoxException(entityClass.getSimpleName() + " object is null");
        }

        return em.merge(entity);
    }

    public static Object remove(final EntityManager em, final Method method, final Object[] args) {
        final Class<?> entityClass = method.getParameterTypes()[0];
        Object entity = args[0];

        if (entity == null) {
            throw new MicroFoxException(entityClass.getSimpleName() + " object is null");
        }

        if (!em.contains(entity)) {
            entity = em.merge(entity);
        }

        em.remove(entity);
        return null;
    }

    private static <T> Object invokeCriteria(EntityManager em, Method method, Object[] args) {
        Class<T> entityClass = getEntityClassFromReturnType(method);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        Criteria criteriaAnn = method.getAnnotation(Criteria.class);

        Predicate predicate = null;
        Integer offset = null;
        Integer maxResults = null;

        // Extract @QueryParam values
        Map<String, Object> queryParams = new HashMap<>();
        java.lang.reflect.Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            java.lang.reflect.Parameter p = parameters[i];
            Object value = args[i];
            boolean ignoreNullValues = criteriaAnn.ignoreNullValues();
            if (p.isAnnotationPresent(QueryParameter.class)) {
                String name = p.getAnnotation(QueryParameter.class).value();
                if (!ignoreNullValues || value != null) {
                    queryParams.put(name, value);
                }
            }

            if (p.isAnnotationPresent(Offset.class)) {
                offset = (Integer) value;
            }

            if (p.isAnnotationPresent(MaxResults.class)) {
                maxResults = (Integer) value;
            }
        }

        // Handle @Criteria
        if (criteriaAnn != null) {
            try {
                Class<? extends CriteriaProvider<?>> providerClass = criteriaAnn.provider();
                CriteriaProvider<?> provider = providerClass.getDeclaredConstructor().newInstance();
                predicate = invokeTypedProvider(cb, root, provider, queryParams);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke CriteriaProvider", e);
            }
        }

        if (predicate != null) {
            query.where(predicate);
        }
        query.select(root);

        TypedQuery<?> typedQuery = em.createQuery(query);
        if (offset != null) typedQuery.setFirstResult(offset);
        if (maxResults != null) typedQuery.setMaxResults(maxResults);

        try {
            return isList(method) ? controlSelectReturnList(method, typedQuery.getResultList()) : typedQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Predicate invokeTypedProvider(CriteriaBuilder cb, Root<?> rawRoot, CriteriaProvider<?> provider, Map<String, Object> queryParams) {
        Root<T> typedRoot = (Root<T>) rawRoot;
        CriteriaProvider<T> typedProvider = (CriteriaProvider<T>) provider;
        return typedProvider.execute(cb, typedRoot, queryParams);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> getEntityClassFromReturnType(Method method) {
        if (Collection.class.isAssignableFrom(method.getReturnType())) {
            ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
            return (Class<T>) pt.getActualTypeArguments()[0];
        } else {
            return (Class<T>) method.getReturnType();
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

        return IntStream.range(0, method.getParameterCount()).mapToObj(i -> new Parameter(annotations[i], types[i], values[i])).collect(Collectors.toList());
    }

    private static Collection<?> controlSelectReturnList(Method method, List<?> list) {
        Class<?> returnType = method.getReturnType();
        if (Set.class.isAssignableFrom(returnType)) {
            return new LinkedHashSet<>(list);
        } else if (Queue.class.isAssignableFrom(returnType)) {
            return new ArrayDeque<>(list);
        }

        return list;
    }
}
