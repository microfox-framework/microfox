package ir.moke.microfox.openapi;

import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.RouteInfo;
import ir.moke.microfox.api.openapi.OpenApiProvider;
import ir.moke.microfox.http.ResourceHolder;
import ir.moke.utils.TtyAsciiCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenApiProviderImpl implements OpenApiProvider, TtyAsciiCodecs {
    private static final Logger logger = LoggerFactory.getLogger(OpenApiProviderImpl.class);

    @Override
    public void registerOpenAPI() {
        /* Redoc & Swagger */
        logger.info("{}{}{}", BACKGROUND_BLUE, "OpenAPI Activated", RESET);
        ResourceHolder.addRoute(new RouteInfo("/docs", HttpMethod.GET, OpenApiServlet::handle));
        ResourceHolder.addRoute(new RouteInfo("/docs/*", HttpMethod.GET, OpenApiServlet::handle));
    }
}
