package ir.moke.microfox.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SqlUtils {
    public static <T> String getColumns(Class<T> tClass) {
        StringBuilder sb = new StringBuilder();
        for (Field declaredField : tClass.getDeclaredFields()) {
            if (Modifier.isStatic(declaredField.getModifiers())) continue;
            if (Modifier.isTransient(declaredField.getModifiers())) continue;
            if (declaredField.isSynthetic()) continue;

            if (declaredField.isAnnotationPresent(Column.class)) {
                String value = declaredField.getDeclaredAnnotation(Column.class).value();
                sb.append(value);
            } else {
                sb.append(declaredField.getName());
            }
            sb.append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static String getValues(Object o) {
        StringBuilder sb = new StringBuilder();
        for (Field declaredField : o.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(declaredField.getModifiers())) continue;
            if (Modifier.isTransient(declaredField.getModifiers())) continue;
            if (declaredField.isSynthetic()) continue;

            try {
                declaredField.setAccessible(true);
                Object value = declaredField.get(o);
                sb.append(formatValue(value));
                declaredField.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            sb.append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    private static String formatValue(Object value) {
        if (value == null) return "NULL";
        if (value instanceof Number) return value.toString();
        return "'" + value.toString().replace("'", "''") + "'";
    }
}
