package ir.moke.microfox.http.filter;

import ir.moke.microfox.api.http.*;
import ir.moke.microfox.api.http.security.Credential;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.http.HttpHelper;
import ir.moke.microfox.http.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ir.moke.microfox.http.HttpHelper.findMatchingRouteInfo;
import static ir.moke.microfox.http.HttpHelper.findMatchingSecurityInfo;

public class SecurityFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public void handle(Request request, Response response, Chain chain) {
        logger.info("Filter security {}", request.uri());
        HttpMethod httpMethod = request.getMethod();

        RouteInfo routeInfo = findMatchingRouteInfo(request.uri(), httpMethod);
        List<SecurityInfo> securities = findMatchingSecurityInfo(request.uri());
        if (routeInfo != null) {
            applySecurity(routeInfo, securities, request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void applySecurity(RouteInfo routeInfo, List<SecurityInfo> securities, Request req, Response resp, Chain chain) {
        try {

            if (securities == null || securities.isEmpty()) {
                chain.doFilter(req, resp); // No security required
                return;
            }

            SecurityInfo securityInfo = securities.getFirst();
            SecurityStrategy strategy = securityInfo.getStrategy();

            Credential credential = strategy.authenticate(req);
            if (credential == null) {
                throw new MicroFoxException(StatusCode.UNAUTHORIZED);
            }

            if (!strategy.authorize(credential, routeInfo.getRoles(), routeInfo.getScopes())) {
                throw new MicroFoxException(StatusCode.FORBIDDEN);
            }

            // Store into SecurityContext for business layer
            ScopedValue.where(SecurityContext.getScopedValue(), credential).run(() -> chain.doFilter(req, resp));
        } catch (Exception e) {
            HttpHelper.handleExceptionMapper(resp.httpServletResponse(), e);
        }
    }
}
