package ir.moke.microfox.api.elastic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class MappingBuilder {
    private final Class<?> rootClass;
    private final Map<String, Object> settings = new LinkedHashMap<>();
    private final Map<String, Object> properties = new LinkedHashMap<>();

    private MappingBuilder(Class<?> rootClass) {
        this.rootClass = rootClass;
    }

    public static MappingBuilder forEntity(Class<?> clazz) {
        if (clazz == null) throw new IllegalArgumentException("Entity class must not be null");
        return new MappingBuilder(clazz);
    }

    public MappingBuilder shards(int count) {
        if (count <= 0) throw new IllegalArgumentException("Shards must be positive");
        settings.put("number_of_shards", count);
        return this;
    }

    public MappingBuilder replicas(int count) {
        if (count < 0) throw new IllegalArgumentException("Replicas must be non-negative");
        settings.put("number_of_replicas", count);
        return this;
    }

    public Map<String, Object> build() {
        processClass(rootClass, properties, new HashSet<>());
        Map<String, Object> mappings = Map.of("properties", properties);
        Map<String, Object> result = new LinkedHashMap<>();
        if (!settings.isEmpty()) result.put("settings", settings);
        result.put("mappings", mappings);
        return result;
    }

    private void processClass(Class<?> clazz, Map<String, Object> target, Set<Class<?>> visited) {
        if (visited.contains(clazz)) return;
        visited.add(clazz);

        for (Field field : clazz.getDeclaredFields()) {

            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) continue;

            field.setAccessible(true);

            ElasticField ef = field.getAnnotation(ElasticField.class);
            ElasticAnalyzer analyzer = field.getAnnotation(ElasticAnalyzer.class);

            String name = (ef != null && !ef.name().isEmpty()) ? ef.name() : field.getName();
            if (name.startsWith("_")) continue;

            Class<?> type = field.getType();

            if (Collection.class.isAssignableFrom(type)) {
                Class<?> genericType = extractGenericType(field);
                if (genericType != null) {
                    boolean isNested = ef == null || ef.value() == ElasticFieldType.AUTO || ef.value() == ElasticFieldType.NESTED;
                    Map<String, Object> nestedProps = new LinkedHashMap<>();
                    processClass(genericType, nestedProps, new HashSet<>(visited));
                    if (isNested) {
                        target.put(name, Map.of("type", "nested", "properties", nestedProps));
                    } else {
                        target.put(name, Map.of("type", "object", "properties", nestedProps));
                    }
                } else {
                    target.put(name, Map.of("type", "object"));
                }
            } else if (isPrimitiveOrWrapper(type) || isBasicType(type)) {
                target.put(name, resolveFieldMapping(type, ef, analyzer));
            } else {
                Map<String, Object> embeddedProps = new LinkedHashMap<>();
                processClass(type, embeddedProps, new HashSet<>(visited));
                target.put(name, Map.of("properties", embeddedProps));
            }
        }
    }

    private Map<String, Object> resolveFieldMapping(Class<?> type, ElasticField ef, ElasticAnalyzer ea) {
        ElasticFieldType esType = determineElasticType(type, ef);
        Map<String, Object> fieldMapping = new LinkedHashMap<>();
        fieldMapping.put("type", esType.name().toLowerCase());

        if (ea != null) {
            if (!ea.analyzer().isEmpty()) fieldMapping.put("analyzer", ea.analyzer());
            if (!ea.searchAnalyzer().isEmpty()) fieldMapping.put("search_analyzer", ea.searchAnalyzer());
        }

        if (esType == ElasticFieldType.TEXT) {
            fieldMapping.put("fields", Map.of(
                    "keyword", Map.of("type", "keyword", "ignore_above", 256)
            ));
        }

        return fieldMapping;
    }

    private ElasticFieldType determineElasticType(Class<?> type, ElasticField ef) {
        if (ef != null && ef.value() != ElasticFieldType.AUTO) return ef.value();

        if (type == String.class) return ElasticFieldType.TEXT;
        if (type == Integer.class || type == int.class) return ElasticFieldType.INTEGER;
        if (type == Long.class || type == long.class) return ElasticFieldType.LONG;
        if (type == Float.class || type == float.class) return ElasticFieldType.FLOAT;
        if (type == Double.class || type == double.class) return ElasticFieldType.DOUBLE;
        if (type == Boolean.class || type == boolean.class) return ElasticFieldType.BOOLEAN;
        if (type == LocalDate.class || type == Instant.class) return ElasticFieldType.DATE;

        return ElasticFieldType.OBJECT;
    }

    private static boolean isBasicType(Class<?> type) {
        return type == String.class || type == LocalDate.class || type == Instant.class;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || PRIMITIVE_MAP.containsKey(type);
    }

    private static Class<?> extractGenericType(Field field) {
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType pt) {
            Type[] args = pt.getActualTypeArguments();
            if (args.length > 0 && args[0] instanceof Class<?>) {
                return (Class<?>) args[0];
            }
        }
        return null;
    }

    private static final Map<Class<?>, String> PRIMITIVE_MAP = Map.of(
            int.class, "integer",
            long.class, "long",
            float.class, "float",
            double.class, "double",
            boolean.class, "boolean"
    );
}