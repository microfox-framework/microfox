package ir.moke.microfox.openapi.servlet;

import io.swagger.v3.oas.models.OpenAPI;
import ir.moke.microfox.api.http.ContentType;
import ir.moke.microfox.openapi.OpenApiGenerator;
import ir.moke.utils.JsonUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class OpenApiServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiServlet.class);
    private String json;

    @Override
    public void init() {
        OpenAPI openAPI = OpenApiGenerator.generate();
        json = JsonUtils.toJson(openAPI);
        logger.info("Initialize OpenAPI");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getRequestURI();
        resp.setCharacterEncoding("UTF-8");
        if (pathInfo.equalsIgnoreCase("/docs/rapidoc-min.js")) {
            resp.setContentType(ContentType.TEXT_JAVASCRIPT.getType());
            resp.getOutputStream().write(rapidocJS().getBytes());
        } else if (pathInfo.endsWith("woff2")) {
            String[] split = pathInfo.split("/");
            String fontFile = split[split.length - 1];
            resp.setContentType(ContentType.FONT_WOFF2.getType());
            resp.getOutputStream().write(fontWOFF2(fontFile).getBytes());
        } else if (pathInfo.equalsIgnoreCase("/docs/openapi.json")) {
            resp.setContentType(ContentType.APPLICATION_JSON.getType());
            resp.getOutputStream().write(json.getBytes());
        } else if (pathInfo.equalsIgnoreCase("/docs/microfox.png")) {
            resp.setContentType(ContentType.IMAGE_PNG.getType());
            byte[] bytes = logoPNG();
            if (bytes != null) {
                resp.getOutputStream().write(bytes);
            }
        } else {
            resp.setContentType(ContentType.TEXT_HTML.getType());
            resp.getOutputStream().write(indexHTML().getBytes());
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
