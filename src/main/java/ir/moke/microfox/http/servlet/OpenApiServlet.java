package ir.moke.microfox.http.servlet;

import io.swagger.v3.oas.models.OpenAPI;
import ir.moke.kafir.utils.JsonUtils;
import ir.moke.microfox.http.OpenApiGenerator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/openapi.json")
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
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }
}
