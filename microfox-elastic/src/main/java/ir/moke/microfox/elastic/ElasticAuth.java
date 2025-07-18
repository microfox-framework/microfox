package ir.moke.microfox.elastic;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class ElasticAuth {
    private ElasticAuth() {
    }

    public static String basic(String user, String pass) {
        String token = user + ":" + pass;
        return "Basic " + Base64.getEncoder()
                .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    public static String apiKey(String id, String key) {
        String token = id + ":" + key;
        return "ApiKey " + Base64.getEncoder()
                .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }
}
