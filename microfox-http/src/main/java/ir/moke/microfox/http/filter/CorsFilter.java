package ir.moke.microfox.http.filter;

import ir.moke.microfox.api.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CorsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    private final Map<CORSHeader, String> valueMap;

    public CorsFilter(Map<CORSHeader, String> valueMap) {
        this.valueMap = valueMap;
    }


    @Override
    public void handle(Request request, Response response, Chain chain) {
        logger.trace("Filter CORS {}", request.uri());
        valueMap.forEach((k, v) -> response.header(k.getValue(), v));
        chain.doFilter(request, response);
    }
}
