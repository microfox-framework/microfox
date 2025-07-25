package ir.moke.microfox.openapi;

import ir.moke.microfox.api.openapi.OpenApiProvider;
import ir.moke.microfox.http.HttpContainer;
import ir.moke.microfox.openapi.servlet.OpenApiServlet;
import ir.moke.microfox.utils.TtyAsciiCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenApiProviderImpl implements OpenApiProvider, TtyAsciiCodecs {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiProviderImpl.class);

    @Override
    public void registerOpenAPI() {
        /* Redoc & Swagger */
        logger.info("{}{}{}", BACKGROUND_BLUE, "OpenAPI Activated", RESET);
        HttpContainer.addServlet(OpenApiServlet.class, "/docs");
        HttpContainer.addServlet(OpenApiServlet.class, "/docs/*");
    }
}
