package ir.moke.microfox.utils;

public class StringUtils {
    public static String normalizeKey(String key) {
        // make dash/underscore/dot interchangeable → all to dot
        return key.replace("-", ".").replace("_", ".");
    }
}
