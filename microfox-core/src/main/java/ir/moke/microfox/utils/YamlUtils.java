package ir.moke.microfox.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ir.moke.microfox.MicrofoxEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static ir.moke.microfox.utils.StringUtils.normalizeKey;

public class YamlUtils {
    private static final Logger logger = LoggerFactory.getLogger(YamlUtils.class);
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    static {
        yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Properties loadAndFlatten() {
        Properties props = new Properties();
        try {
            Enumeration<URL> resources = MicrofoxEnvironment.class.getClassLoader().getResources("application.yaml");
            List<URL> urls = Collections.list(resources).reversed(); // only for apply application config after accept all default values
            for (URL url : urls) {
                try (InputStream is = url.openStream()) {
                    Map<String, Object> yamlMap = yamlMapper.readValue(is, new TypeReference<>() {
                    });
                    flattenMap("", yamlMap, props);
                }
            }

        } catch (IOException e) {
            logger.error("Unknown error", e);
            System.exit(0);
        }
        return props;
    }

    @SuppressWarnings("unchecked")
    private static void flattenMap(String prefix, Map<String, Object> map, Properties props) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = normalizeKey(prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey());
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenMap(key, (Map<String, Object>) value, props);
            } else if (value instanceof List<?> list) {
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item instanceof Map) {
                        flattenMap(key + "[" + i + "]", (Map<String, Object>) item, props);
                    } else {
                        props.put(key + "[" + i + "]", item);
                    }
                }
            } else {
                props.put(key, value == null ? "" : String.valueOf(value));
            }
        }
    }
}