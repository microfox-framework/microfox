package ir.moke.microfox.http.proxy;

import ir.moke.microfox.api.http.annotation.*;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.exception.MicroFoxParameterException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.*;
import java.util.*;

public class BeanParamBinder {

    static boolean isBindableField(Field field) {
        return field.isAnnotationPresent(QueryParam.class)
                || field.isAnnotationPresent(PathParam.class)
                || field.isAnnotationPresent(HeaderParam.class)
                || field.isAnnotationPresent(CookieParam.class)
                || field.isAnnotationPresent(QueryParams.class);
    }

    static Object resolveBeanFieldValue(Field field, HttpServletRequest request) {
        validateBindingAnnotations(field);
        boolean resolved = false;
        String rawValue;
        String[] rawValues = null;

        QueryParam queryParam = field.getAnnotation(QueryParam.class);
        if (queryParam != null) {
            rawValues = request.getParameterValues(queryParam.value());
            resolved = true;
        }

        PathParam pathParam = field.getAnnotation(PathParam.class);
        if (pathParam != null) {
            rawValue = RequestHelper.pathParam(pathParam.value(), request);
            rawValues = rawValue == null ? null : new String[]{rawValue};
            resolved = true;
        }

        HeaderParam headerParam = field.getAnnotation(HeaderParam.class);
        if (headerParam != null) {
            Enumeration<String> headers = request.getHeaders(headerParam.value());
            List<String> list = headers == null ? List.of() : Collections.list(headers);
            rawValues = list.isEmpty() ? null : list.toArray(String[]::new);
            resolved = true;
        }

        CookieParam cookieParam = field.getAnnotation(CookieParam.class);
        if (cookieParam != null) {
            rawValue = RequestHelper.cookie(cookieParam.value(), request);
            rawValues = rawValue == null ? null : new String[]{rawValue};
            resolved = true;
        }

        if (field.isAnnotationPresent(QueryParams.class)) return convertQueryParamsMap(field, request);

        if (!resolved) throw new MicroFoxParameterException("Field is not bindable: %s".formatted(field.getName()));

        DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
        if ((rawValues == null || rawValues.length == 0 || allBlank(rawValues)) && defaultValue != null)
            rawValues = new String[]{defaultValue.value()};

        Required required = field.getAnnotation(Required.class);
        if ((rawValues == null || rawValues.length == 0 || allBlank(rawValues)) && required != null)
            throw new MicroFoxParameterException("Required parameter is missing: " + field.getName());

        if (rawValues == null || rawValues.length == 0 || allBlank(rawValues)) {
            if (isContainerType(field.getType())) return convertValue(new String[0], field);
            return getNullValueForType(field.getType());
        }
        return convertValue(rawValues, field);
    }

    static <U> U createRecordInstance(Class<U> recordClass, HttpServletRequest request) {
        try {
            RecordComponent[] components = recordClass.getRecordComponents();
            Object[] args = new Object[components.length];
            Class<?>[] types = new Class<?>[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                types[i] = component.getType();
                args[i] = resolveRecordComponentValue(component, request);
            }

            Constructor<U> constructor = recordClass.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new MicroFoxException("Cannot create record bean: " + recordClass.getName(), e);
        }
    }

    static Object resolveRecordComponentValue(RecordComponent component, HttpServletRequest request) {
        boolean resolved = false;
        String rawValue;
        String[] rawValues = null;

        QueryParam queryParam = component.getAnnotation(QueryParam.class);
        if (queryParam != null) {
            rawValues = request.getParameterValues(queryParam.value());
            resolved = true;
        }

        PathParam pathParam = component.getAnnotation(PathParam.class);
        if (pathParam != null) {
            rawValue = RequestHelper.pathParam(pathParam.value(), request);
            rawValues = rawValue == null ? null : new String[]{rawValue};
            resolved = true;
        }

        HeaderParam headerParam = component.getAnnotation(HeaderParam.class);
        if (headerParam != null) {
            Enumeration<String> headers = request.getHeaders(headerParam.value());
            List<String> list = headers == null ? List.of() : Collections.list(headers);
            rawValues = list.isEmpty() ? null : list.toArray(String[]::new);
            resolved = true;
        }

        CookieParam cookieParam = component.getAnnotation(CookieParam.class);
        if (cookieParam != null) {
            rawValue = RequestHelper.cookie(cookieParam.value(), request);
            rawValues = rawValue == null ? null : new String[]{rawValue};
            resolved = true;
        }

        if (component.isAnnotationPresent(QueryParams.class)) return convertQueryParamsMap(component, request);
        if (!resolved) return getNullValueForType(component.getType());

        DefaultValue defaultValue = component.getAnnotation(DefaultValue.class);
        if ((rawValues == null || rawValues.length == 0 || allBlank(rawValues)) && defaultValue != null)
            rawValues = new String[]{defaultValue.value()};

        Required required = component.getAnnotation(Required.class);
        if ((rawValues == null || rawValues.length == 0 || allBlank(rawValues)) && required != null)
            throw new MicroFoxParameterException("Required parameter is missing: " + component.getName());

        if (rawValues == null || rawValues.length == 0 || allBlank(rawValues)) {
            if (isContainerType(component.getType())) return convertValue(new String[0], component);
            return getNullValueForType(component.getType());
        }

        return convertValue(rawValues, component);
    }

    // --- Common Conversion Dispatchers ---

    private static Object convertValue(String[] values, Field field) {
        return convertValueGeneric(values, field.getType(), field.getGenericType(), field.getName());
    }

    private static Object convertValue(String[] values, RecordComponent component) {
        return convertValueGeneric(values, component.getType(), component.getGenericType(), component.getName());
    }

    private static Object convertValueGeneric(String[] values, Class<?> type, Type genericType, String name) {
        if (type == Optional.class) return convertOptional(values, genericType);
        if (type == OptionalInt.class) return convertOptionalInt(values);
        if (type == OptionalLong.class) return convertOptionalLong(values);
        if (type == OptionalDouble.class) return convertOptionalDouble(values);
        if (type.isArray()) return convertArray(values, type.getComponentType());

        if (SortedSet.class.isAssignableFrom(type) || NavigableSet.class.isAssignableFrom(type))
            return convertSortedSet(values, genericType, name);
        if (Set.class.isAssignableFrom(type))
            return convertSet(values, type, genericType, name);
        if (List.class.isAssignableFrom(type) || type == Collection.class || type == Iterable.class || type == Queue.class || type == Deque.class)
            return convertList(values, type, genericType, name);

        if (values == null || values.length == 0) return getNullValueForType(type);
        return convertValue(values[0], type);
    }

    // --- Collection Helpers ---

    private static Object convertList(String[] values, Class<?> type, Type genericType, String fieldName) {
        Class<?> elementType = getFirstGenericClass(genericType, fieldName);
        List<String> flattened = flattenValues(values);
        List<Object> result = createListInstance(type, flattened.size());
        for (String raw : flattened) result.add(convertValue(raw, elementType));
        return result;
    }

    private static Object convertSet(String[] values, Class<?> type, Type genericType, String fieldName) {
        Class<?> elementType = getFirstGenericClass(genericType, fieldName);
        List<String> flattened = flattenValues(values);
        Set<Object> result = createSetInstance(type);
        for (String raw : flattened) result.add(convertValue(raw, elementType));
        return result;
    }

    private static Object convertSortedSet(String[] values, Type genericType, String fieldName) {
        Class<?> elementType = getFirstGenericClass(genericType, fieldName);
        List<String> flattened = flattenValues(values);
        SortedSet<Object> result = new TreeSet<>();
        for (String raw : flattened) {
            Object converted = convertValue(raw, elementType);
            if (!(converted instanceof Comparable))
                throw new MicroFoxParameterException("SortedSet element type must be Comparable: " + elementType.getName());
            result.add(converted);
        }
        return result;
    }

    private static Object convertOptional(String[] values, Type genericType) {
        Class<?> elementType = getFirstGenericClass(genericType, "Optional");
        List<String> flattened = flattenValues(values);
        if (flattened.isEmpty()) return Optional.empty();
        return Optional.ofNullable(convertValue(flattened.getFirst(), elementType));
    }

    private static Class<?> getFirstGenericClass(Type genericType, String fieldName) {
        if (!(genericType instanceof ParameterizedType pt)) return String.class;
        Type[] args = pt.getActualTypeArguments();
        if (args.length == 0) return String.class;
        if (args[0] instanceof Class<?> clazz) return clazz;
        if (args[0] instanceof ParameterizedType npt && npt.getRawType() instanceof Class<?> rc) return rc;
        throw new MicroFoxParameterException("Unsupported generic type for: " + fieldName);
    }

    // --- Map Support ---

    private static Object convertQueryParamsMap(Field field, HttpServletRequest request) {
        return convertQueryParamsMapGeneric(field.getType(), field.getGenericType(), field.getName(), request);
    }

    private static Object convertQueryParamsMap(RecordComponent component, HttpServletRequest request) {
        return convertQueryParamsMapGeneric(component.getType(), component.getGenericType(), component.getName(), request);
    }

    private static Object convertQueryParamsMapGeneric(Class<?> type, Type genericType, String name, HttpServletRequest request) {
        if (!Map.class.isAssignableFrom(type))
            throw new MicroFoxParameterException("@QueryParams can only be used on Map fields: " + name);

        Class<?> keyType = getMapKeyClass(genericType);
        Class<?> valueType = getMapValueClass(genericType);
        if (keyType != String.class)
            throw new MicroFoxParameterException("@QueryParams Map key must be String: " + name);

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (valueType == String.class) {
            Map<String, String> result = new LinkedHashMap<>();
            parameterMap.forEach((k, v) -> result.put(k, v != null && v.length > 0 ? v[0] : null));
            return result;
        }
        if (List.class.isAssignableFrom(valueType) || Collection.class.isAssignableFrom(valueType)) {
            Map<String, List<String>> result = new LinkedHashMap<>();
            parameterMap.forEach((k, v) -> result.put(k, flattenValues(v)));
            return result;
        }
        if (String[].class == valueType) return new LinkedHashMap<>(parameterMap);
        throw new MicroFoxParameterException("Unsupported @QueryParams Map value type: " + valueType.getName());
    }

    private static Class<?> getMapKeyClass(Type genericType) {
        if (!(genericType instanceof ParameterizedType pt)) return String.class;
        return (Class<?>) pt.getActualTypeArguments()[0];
    }

    private static Class<?> getMapValueClass(Type genericType) {
        if (!(genericType instanceof ParameterizedType pt)) return String.class;
        Type valType = pt.getActualTypeArguments()[1];
        if (valType instanceof Class<?> c) return c;
        if (valType instanceof ParameterizedType npt) return (Class<?>) npt.getRawType();
        return String.class;
    }

    // --- Static Conversion & Helpers ---

    private static Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(value);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value);
        if (targetType == float.class || targetType == Float.class) return Float.parseFloat(value);
        if (targetType == boolean.class || targetType == Boolean.class) return parseBooleanStrict(value);
        if (targetType == short.class || targetType == Short.class) return Short.parseShort(value);
        if (targetType == byte.class || targetType == Byte.class) return Byte.parseByte(value);
        if (targetType.isEnum()) return parseEnum(value, targetType);
        if (UUID.class == targetType) return UUID.fromString(value);
        if (LocalTime.class == targetType) return LocalTime.parse(value);
        if (LocalDate.class == targetType) return LocalDate.parse(value);
        if (LocalDateTime.class == targetType) return LocalDateTime.parse(value);
        if (OffsetDateTime.class == targetType) return OffsetDateTime.parse(value);
        if (BigDecimal.class == targetType) return new BigDecimal(value);
        if (BigInteger.class == targetType) return new BigInteger(value);
        if (ZonedDateTime.class == targetType) return ZonedDateTime.parse(value);
        if (Instant.class == targetType) return Instant.parse(value);
        if (Year.class == targetType) return Year.parse(value);
        if (YearMonth.class == targetType) return YearMonth.parse(value);
        if (MonthDay.class == targetType) return MonthDay.parse(value);
        if (Duration.class == targetType) return Duration.parse(value);
        if (Period.class == targetType) return Period.parse(value);
        if (Path.class == targetType) return Path.of(value);
        if (File.class == targetType) return new File(value);
        if (URI.class == targetType) return URI.create(value);
        if (java.sql.Date.class == targetType) return java.sql.Date.valueOf(LocalDate.parse(value));
        if (java.sql.Time.class == targetType) return java.sql.Time.valueOf(LocalTime.parse(value));
        if (java.sql.Timestamp.class == targetType) return java.sql.Timestamp.valueOf(LocalDateTime.parse(value));
        if (Locale.class == targetType) return Locale.forLanguageTag(value);
        if (Currency.class == targetType) return Currency.getInstance(value);
        if (ZoneId.class == targetType) return ZoneId.of(value);
        if (ZoneOffset.class == targetType) return ZoneOffset.of(value);

        if (TimeZone.class == targetType) {
            TimeZone timeZone = TimeZone.getTimeZone(value);
            boolean valid = Arrays.asList(TimeZone.getAvailableIDs()).contains(value);
            if (!valid && !"GMT".equals(value) && !"UTC".equals(value))
                throw new MicroFoxParameterException("Invalid TimeZone value: " + value);
            return timeZone;
        }

        if (Date.class == targetType) {
            try {
                return Date.from(Instant.parse(value));
            } catch (Exception e) {
                throw new MicroFoxParameterException("Invalid Date: " + value);
            }
        }

        if (targetType == char.class || targetType == Character.class) {
            if (value.length() != 1) throw new MicroFoxParameterException("Invalid char: " + value);
            return value.charAt(0);
        }

        if (URL.class == targetType) {
            try {
                return URI.create(value).toURL();
            } catch (Exception e) {
                throw new MicroFoxParameterException("Invalid URL: " + value);
            }
        }

        if (InetAddress.class == targetType) {
            try {
                return InetAddress.getByName(value);
            } catch (Exception e) {
                throw new MicroFoxParameterException("Invalid address: " + value);
            }
        }

        Object converted = tryStaticStringFactory(value, targetType, "valueOf");
        if (converted != null) return converted;
        converted = tryStaticStringFactory(value, targetType, "fromString");
        if (converted != null) return converted;
        converted = tryStringConstructor(value, targetType);
        if (converted != null) return converted;

        throw new MicroFoxParameterException("Unsupported type: " + targetType.getName());
    }

    private static Object getNullValueForType(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        return 0;
    }

    static <U> U createInstance(Class<U> beanClass) {
        try {
            Constructor<U> constructor = beanClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new MicroFoxException("No-args constructor required: " + beanClass.getName(), e);
        }
    }

    static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }

    private static Object tryStaticStringFactory(String value, Class<?> targetType, String methodName) {
        try {
            Method method = targetType.getMethod(methodName, String.class);
            if (!Modifier.isStatic(method.getModifiers())) return null;
            return method.invoke(null, value);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object tryStringConstructor(String value, Class<?> targetType) {
        try {
            Constructor<?> constructor = targetType.getConstructor(String.class);
            return constructor.newInstance(value);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object parseEnum(String value, Class<?> targetType) {
        for (Object constant : targetType.getEnumConstants()) {
            Enum<?> enumValue = (Enum<?>) constant;
            if (enumValue.name().equalsIgnoreCase(value)) return enumValue;
        }
        throw new MicroFoxParameterException("Invalid enum value: " + value);
    }

    private static Boolean parseBooleanStrict(String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new MicroFoxParameterException("Invalid boolean: " + value);
    }

    private static boolean allBlank(String[] values) {
        if (values == null) return true;
        for (String v : values) if (v != null && !v.isBlank()) return false;
        return true;
    }

    private static List<String> flattenValues(String[] values) {
        List<String> result = new ArrayList<>();
        if (values == null) return result;
        for (String v : values) {
            if (v == null) continue;
            for (String part : v.split(",")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) result.add(trimmed);
            }
        }
        return result;
    }

    private static List<Object> createListInstance(Class<?> targetType, int size) {
        if (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())) return new ArrayList<>(size);
        if (targetType == LinkedList.class) return new LinkedList<>();
        return new ArrayList<>(size);
    }

    private static Set<Object> createSetInstance(Class<?> targetType) {
        if (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())) return new LinkedHashSet<>();
        if (targetType == TreeSet.class) return new TreeSet<>();
        return new LinkedHashSet<>();
    }

    private static boolean isContainerType(Class<?> type) {
        return type == Optional.class || type == OptionalInt.class || type == OptionalLong.class
                || type == OptionalDouble.class || type.isArray() || Collection.class.isAssignableFrom(type);
    }

    private static void validateBindingAnnotations(Field field) {
        int count = 0;
        if (field.isAnnotationPresent(QueryParam.class)) count++;
        if (field.isAnnotationPresent(PathParam.class)) count++;
        if (field.isAnnotationPresent(HeaderParam.class)) count++;
        if (field.isAnnotationPresent(CookieParam.class)) count++;
        if (field.isAnnotationPresent(QueryParams.class)) count++;
        if (count > 1) throw new MicroFoxParameterException("Multiple binding annotations on: " + field.getName());
    }

    private static OptionalInt convertOptionalInt(String[] values) {
        List<String> list = flattenValues(values);
        return list.isEmpty() ? OptionalInt.empty() : OptionalInt.of(Integer.parseInt(list.getFirst()));
    }

    private static OptionalLong convertOptionalLong(String[] values) {
        List<String> list = flattenValues(values);
        return list.isEmpty() ? OptionalLong.empty() : OptionalLong.of(Long.parseLong(list.getFirst()));
    }

    private static OptionalDouble convertOptionalDouble(String[] values) {
        List<String> list = flattenValues(values);
        return list.isEmpty() ? OptionalDouble.empty() : OptionalDouble.of(Double.parseDouble(list.getFirst()));
    }

    private static Object convertArray(String[] values, Class<?> componentType) {
        List<String> flattened = flattenValues(values);
        Object array = Array.newInstance(componentType, flattened.size());
        for (int i = 0; i < flattened.size(); i++) {
            Object converted = convertValue(flattened.get(i), componentType);
            Array.set(array, i, converted);
        }

        return array;
    }
}
