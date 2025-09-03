package ir.moke.microfox.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class StringUtils {
    public static String normalizeKey(String key) {
        // make dash/underscore/dot interchangeable → all to dot
        return key.replace("-", ".").replace("_", ".");
    }

    public static Stream<String> readAllLines(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines();
    }
}
