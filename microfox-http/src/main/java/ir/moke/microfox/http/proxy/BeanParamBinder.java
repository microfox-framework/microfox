package ir.moke.microfox.http.proxy;

import ir.moke.microfox.api.http.BeanParamValue;
import ir.moke.microfox.api.http.annotation.*;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.exception.MicroFoxParameterException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BeanParamBinder {

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
        if (!resolved) return BeanParamValue.UNRESOLVED;

        DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);

        if ((rawValues == null || rawValues.length == 0 || allBlank(rawValues)) && defaultValue != null)
            rawValues = new String[]{defaultValue.value()};

        Required required = field.getAnnotation(Required.class);
        if ((rawValues == null || rawValues.length == 0 || allBlank(rawValues)) && required != null)
            throw new MicroFoxParameterException("Required parameter is missing: " + field.getName());

        if (rawValues == null || rawValues.length == 0 || allBlank(rawValues)) {
            if (isContainerType(field.getType())) {
                return convertValue(new String[0], field);
            }

            return getNullValueForType(field.getType());
        }
        return convertValue(rawValues, field);
    }

    private static OptionalInt convertOptionalInt(String[] values) {
        List<String> flattened = flattenValues(values);
        if (flattened.isEmpty()) return OptionalInt.empty();
        try {
            return OptionalInt.of(Integer.parseInt(flattened.getFirst()));
        } catch (NumberFormatException e) {
            throw new MicroFoxParameterException("Invalid integer for OptionalInt: " + flattened.getFirst());
        }
    }


    private static OptionalLong convertOptionalLong(String[] values) {
        List<String> flattened = flattenValues(values);
        if (flattened.isEmpty()) return OptionalLong.empty();
        return OptionalLong.of(Long.parseLong(flattened.getFirst()));
    }

    private static OptionalDouble convertOptionalDouble(String[] values) {
        List<String> flattened = flattenValues(values);
        if (flattened.isEmpty()) return OptionalDouble.empty();
        return OptionalDouble.of(Double.parseDouble(flattened.getFirst()));
    }

    private static boolean isContainerType(Class<?> type) {
        return type == Optional.class
                || type == OptionalInt.class
                || type == OptionalLong.class
                || type == OptionalDouble.class
                || type.isArray()
                || Collection.class.isAssignableFrom(type);
    }


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
            if (!valid && !"GMT".equals(value) && !"UTC".equals(value)) {
                throw new MicroFoxParameterException("Invalid TimeZone value: %s".formatted(value));
            }

            return timeZone;
        }

        if (Date.class == targetType) {
            try {
                return Date.from(Instant.parse(value));
            } catch (DateTimeParseException e) {
                throw new MicroFoxParameterException("Invalid Date value: %s".formatted(value));
            }
        }

        if (targetType == char.class || targetType == Character.class) {
            if (value.length() != 1) {
                throw new MicroFoxParameterException("Cannot convert value to char: %s".formatted(value));
            }
            return value.charAt(0);
        }

        if (URL.class == targetType) {
            try {
                return URI.create(value).toURL();
            } catch (MalformedURLException e) {
                throw new MicroFoxParameterException("Invalid URL: %s".formatted(value));
            }
        }

        if (InetAddress.class == targetType) {
            try {
                return InetAddress.getByName(value);
            } catch (UnknownHostException e) {
                throw new MicroFoxParameterException("Invalid address: " + value);
            }
        }

        Object converted = tryStaticStringFactory(value, targetType, "valueOf");
        if (converted != null) return converted;

        converted = tryStaticStringFactory(value, targetType, "fromString");
        if (converted != null) return converted;

        converted = tryStringConstructor(value, targetType);
        if (converted != null) return converted;

        throw new MicroFoxParameterException("Unsupported bean parameter type: %s".formatted(targetType.getName()));
    }

    private static Object getNullValueForType(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        if (type == char.class) return '\0';
        return null;
    }

    static <U> U createInstance(Class<U> beanClass) {
        try {
            Constructor<U> constructor = beanClass.getDeclaredConstructor();
            boolean accessible = constructor.canAccess(null);
            constructor.setAccessible(true);
            U instance = constructor.newInstance();
            constructor.setAccessible(accessible);
            return instance;
        } catch (Exception e) {
            throw new MicroFoxException("BeanParam class must have a no-args constructor: %s".formatted(beanClass.getName()), e);
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
            int modifiers = method.getModifiers();
            if (!Modifier.isStatic(modifiers)) return null;
            if (!targetType.isAssignableFrom(method.getReturnType())) return null;
            return method.invoke(null, value);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (target instanceof RuntimeException runtimeException) throw runtimeException;
            throw new MicroFoxParameterException("Could not convert value '%s' using %s.%s (String)".formatted(value, targetType.getName(), methodName));
        } catch (Exception e) {
            throw new MicroFoxParameterException("Could not convert value '%s' using %s.%s (String)".formatted(value, targetType.getName(), methodName));
        }
    }

    private static Object tryStringConstructor(String value, Class<?> targetType) {
        try {
            Constructor<?> constructor = targetType.getConstructor(String.class);
            return constructor.newInstance(value);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (target instanceof RuntimeException runtimeException) throw runtimeException;
            throw new MicroFoxParameterException("Could not convert value '%s' using constructor %s (String)".formatted(value, targetType.getName()));
        } catch (Exception e) {
            throw new MicroFoxParameterException("Could not convert value '%s' using constructor %s (String)".formatted(value, targetType.getName()));
        }
    }

    private static Object parseEnum(String value, Class<?> targetType) {
        for (Object constant : targetType.getEnumConstants()) {
            Enum<?> enumValue = (Enum<?>) constant;
            if (enumValue.name().equals(value)) return enumValue;
            if (enumValue.name().equalsIgnoreCase(value)) return enumValue;
        }

        throw new MicroFoxParameterException("Invalid enum value '%s' for type %s".formatted(value, targetType.getName()));
    }

    private static Boolean parseBooleanStrict(String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;

        throw new MicroFoxParameterException("Invalid boolean value: %s".formatted(value));
    }

    private static boolean allBlank(String[] values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return false;
            }
        }

        return true;
    }

    private static Object convertValue(String[] values, Field field) {
        Class<?> targetType = field.getType();
        if (targetType == Optional.class) return convertOptional(values, field);
        if (targetType == OptionalInt.class) return convertOptionalInt(values);
        if (targetType == OptionalLong.class) return convertOptionalLong(values);
        if (targetType == OptionalDouble.class) return convertOptionalDouble(values);
        if (targetType.isArray()) return convertArray(values, targetType.getComponentType());
        if (SortedSet.class.isAssignableFrom(targetType) || NavigableSet.class.isAssignableFrom(targetType))
            return convertSortedSet(values, field);
        if (Set.class.isAssignableFrom(targetType)) return convertSet(values, field);

        if (List.class.isAssignableFrom(targetType)
                || targetType == Collection.class
                || targetType == Iterable.class
                || targetType == Queue.class
                || targetType == Deque.class)
            return convertList(values, field);

        if (values == null || values.length == 0) return getNullValueForType(targetType);
        return convertValue(values[0], targetType);
    }

    private static List<String> flattenValues(String[] values) {
        List<String> result = new ArrayList<>();

        if (values == null) return result;

        for (String value : values) {
            if (value == null) {
                continue;
            }

            String[] parts = value.split(",");

            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) result.add(trimmed);
            }
        }

        return result;
    }

    private static Class<?> getFirstGenericClass(Field field) {
        Type genericType = field.getGenericType();

        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return String.class;
        }

        Type[] arguments = parameterizedType.getActualTypeArguments();
        if (arguments.length == 0) return String.class;
        Type argument = arguments[0];
        if (argument instanceof Class<?> clazz) return clazz;

        if (argument instanceof ParameterizedType nestedParameterizedType) {
            Type rawType = nestedParameterizedType.getRawType();
            if (rawType instanceof Class<?> rawClass) return rawClass;
        }

        throw new MicroFoxParameterException("Unsupported generic type for field: %s".formatted(field.getName()));
    }

    private static Object convertList(String[] values, Field field) {
        Class<?> elementType = getFirstGenericClass(field);
        List<String> flattened = flattenValues(values);
        List<Object> result = createListInstance(field.getType(), flattened.size());
        for (String raw : flattened) result.add(convertValue(raw, elementType));
        return result;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> createListInstance(Class<?> targetType, int size) {
        if (targetType == List.class || targetType == Collection.class || targetType == Iterable.class)
            return new ArrayList<>(size);
        if (targetType == ArrayList.class) return new ArrayList<>(size);
        if (targetType == LinkedList.class) return new LinkedList<>();
        if (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())) return new ArrayList<>(size);
        try {
            Object instance = targetType.getDeclaredConstructor().newInstance();
            if (!(instance instanceof List<?>))
                throw new MicroFoxParameterException("Type is not a List: %s".formatted(targetType.getName()));
            return (List<Object>) instance;
        } catch (Exception e) {
            throw new MicroFoxParameterException("Cannot create List type: %s".formatted(targetType.getName()));
        }
    }

    private static Object convertSet(String[] values, Field field) {
        Class<?> elementType = getFirstGenericClass(field);
        List<String> flattened = flattenValues(values);
        Set<Object> result = createSetInstance(field.getType());
        for (String raw : flattened) result.add(convertValue(raw, elementType));
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Set<Object> createSetInstance(Class<?> targetType) {
        if (targetType == Set.class || targetType == Collection.class || targetType == Iterable.class)
            return new LinkedHashSet<>();
        if (targetType == HashSet.class || targetType == LinkedHashSet.class) return new LinkedHashSet<>();
        if (targetType == TreeSet.class || SortedSet.class.isAssignableFrom(targetType) || NavigableSet.class.isAssignableFrom(targetType))
            return new TreeSet<>();
        if (targetType.isInterface() || Modifier.isAbstract(targetType.getModifiers())) return new LinkedHashSet<>();

        try {
            Object instance = targetType.getDeclaredConstructor().newInstance();
            if (!(instance instanceof Set<?>))
                throw new MicroFoxParameterException("Type is not a Set: %s".formatted(targetType.getName()));
            return (Set<Object>) instance;
        } catch (Exception e) {
            throw new MicroFoxParameterException("Cannot create Set type: %s".formatted(targetType.getName()));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object convertSortedSet(String[] values, Field field) {
        Class<?> elementType = getFirstGenericClass(field);
        List<String> flattened = flattenValues(values);
        SortedSet result = new TreeSet();
        for (String raw : flattened) {
            Object converted = convertValue(raw, elementType);
            if (!(converted instanceof Comparable))
                throw new MicroFoxParameterException("SortedSet element type must be Comparable: %s".formatted(elementType.getName()));
            result.add(converted);
        }

        return result;
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

    private static Object convertOptional(String[] values, Field field) {
        Class<?> elementType = getFirstGenericClass(field);
        List<String> flattened = flattenValues(values);
        if (flattened.isEmpty()) return Optional.empty();
        Object converted = convertValue(flattened.getFirst(), elementType);
        return Optional.ofNullable(converted);
    }


    private static Object convertQueryParamsMap(Field field, HttpServletRequest request) {
        if (!Map.class.isAssignableFrom(field.getType())) {
            throw new MicroFoxParameterException("@QueryParams can only be used on Map fields: %s".formatted(field.getName()));
        }

        Class<?> keyType = getMapKeyClass(field);
        Class<?> valueType = getMapValueClass(field);

        if (keyType != String.class)
            throw new MicroFoxParameterException("@QueryParams Map key type must be String: %s".formatted(field.getName()));

        Map<String, String[]> parameterMap = request.getParameterMap();

        if (valueType == String.class) {
            Map<String, String> result = new LinkedHashMap<>();

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String[] values = entry.getValue();
                result.put(entry.getKey(), values != null && values.length > 0 ? values[0] : null);
            }

            return result;
        }

        if (List.class.isAssignableFrom(valueType) || Collection.class.isAssignableFrom(valueType)) {
            Map<String, List<String>> result = new LinkedHashMap<>();

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                result.put(entry.getKey(), flattenValues(entry.getValue()));
            }

            return result;
        }

        if (String[].class == valueType) return new LinkedHashMap<>(parameterMap);
        throw new MicroFoxParameterException("Unsupported @QueryParams Map value type: %s".formatted(valueType.getName()));
    }

    private static Class<?> getMapKeyClass(Field field) {
        Type genericType = field.getGenericType();

        if (!(genericType instanceof ParameterizedType parameterizedType)) return String.class;
        Type keyType = parameterizedType.getActualTypeArguments()[0];
        if (keyType instanceof Class<?> clazz) return clazz;
        throw new MicroFoxParameterException("Unsupported Map key type for field: %s".formatted(field.getName()));
    }

    private static Class<?> getMapValueClass(Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType parameterizedType)) return String.class;
        Type valueType = parameterizedType.getActualTypeArguments()[1];
        if (valueType instanceof Class<?> clazz) return clazz;
        if (valueType instanceof ParameterizedType nested) {
            Type rawType = nested.getRawType();
            if (rawType instanceof Class<?> rawClass) return rawClass;
        }

        throw new MicroFoxParameterException("Unsupported Map value type for field: %s".formatted(field.getName()));
    }

    private static void validateBindingAnnotations(Field field) {
        int count = 0;
        if (field.isAnnotationPresent(QueryParam.class)) count++;
        if (field.isAnnotationPresent(PathParam.class)) count++;
        if (field.isAnnotationPresent(HeaderParam.class)) count++;
        if (field.isAnnotationPresent(CookieParam.class)) count++;
        if (field.isAnnotationPresent(QueryParams.class)) count++;

        if (count > 1) {
            throw new MicroFoxParameterException("Multiple binding annotations found on field '%s'. Only one of @QueryParam, @PathParam, @HeaderParam, @CookieParam, or @QueryParams is allowed.".formatted(field.getName()));
        }
    }
}
