package ir.moke.microfox.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.models.OpenAPI;
import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class OpenApiServlet {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiServlet.class);
    private static final String json;

    static {
        OpenAPI openAPI = OpenApiGenerator.generate();
        try {
            json = JsonUtils.toJson(openAPI);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        logger.info("Initialize OpenAPI");
    }


    public static void handle(Request req, Response resp) throws Throwable {
        String pathInfo = req.uri();
        if (pathInfo.equalsIgnoreCase("/docs/rapidoc-min.js")) {
            resp.contentType(ContentType.TEXT_JAVASCRIPT);
            resp.body(rapidocJS().getBytes());
        } else if (pathInfo.endsWith("woff2")) {
            String[] split = pathInfo.split("/");
            String fontFile = split[split.length - 1];
            resp.contentType(ContentType.FONT_WOFF2);
            resp.body(fontWOFF2(fontFile).getBytes());
        } else if (pathInfo.equalsIgnoreCase("/docs/openapi.json")) {
            resp.contentType(ContentType.APPLICATION_JSON);
            resp.body(json.getBytes());
        } else if (pathInfo.equalsIgnoreCase("/docs/microfox.png")) {
            resp.contentType(ContentType.IMAGE_PNG);
            byte[] bytes = logoPNG();
            if (bytes != null) {
                resp.body(bytes);
            }
        } else {
            resp.contentType(ContentType.TEXT_HTML);
            resp.body(indexHTML().getBytes());
        }
    }

    private static String rapidocJS() {
        try (InputStream inputStream = OpenApiServlet.class.getClassLoader().getResourceAsStream("open-api/rapidoc-min.js")) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String indexHTML() {
        try (InputStream inputStream = OpenApiServlet.class.getClassLoader().getResourceAsStream("open-api/index.html")) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String fontWOFF2(String fontFile) {
        try (InputStream inputStream = OpenApiServlet.class.getClassLoader().getResourceAsStream("open-api/%s".formatted(fontFile))) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] logoPNG() {
        try (InputStream inputStream = OpenApiServlet.class.getClassLoader().getResourceAsStream("open-api/%s".formatted("microfox.png"))) {
            if (inputStream != null) {
                return inputStream.readAllBytes();
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
