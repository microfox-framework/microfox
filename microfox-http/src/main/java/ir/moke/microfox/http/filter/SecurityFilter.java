package ir.moke.microfox.http.filter;

import ir.moke.microfox.api.http.HttpMethod;
import ir.moke.microfox.api.http.RouteInfo;
import ir.moke.microfox.api.http.SecurityInfo;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.api.http.security.Credential;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.http.HttpHelper;
import ir.moke.microfox.http.SecurityContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ir.moke.microfox.http.HttpHelper.findMatchingRouteInfo;
import static ir.moke.microfox.http.HttpHelper.findMatchingSecurityInfo;

public class SecurityFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpMethod httpMethod = HttpMethod.valueOf(req.getMethod().toUpperCase());

        RouteInfo routeInfo = findMatchingRouteInfo(req.getRequestURI(), httpMethod);
        List<SecurityInfo> securities = findMatchingSecurityInfo(req.getRequestURI());
        if (routeInfo != null) {
            applySecurity(routeInfo,securities, req, resp, chain);
        } else {
            doChain(req, resp, chain);
        }
    }

    private void applySecurity(RouteInfo routeInfo,List<SecurityInfo> securities, HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        if (securities == null || securities.isEmpty()) {
            doChain(req, resp, chain); // No security required
            return;
        }

        SecurityInfo securityInfo = securities.getFirst();
        SecurityStrategy strategy = securityInfo.getStrategy();

        Credential credential = strategy.authenticate(HttpHelper.getRequest(req));
        if (credential == null) {
            throw new MicroFoxException(StatusCode.UNAUTHORIZED);
        }

        if (!strategy.authorize(credential, routeInfo.getRoles(), routeInfo.getScopes())) {
            throw new MicroFoxException(StatusCode.FORBIDDEN);
        }

        // Store into SecurityContext for business layer
        ScopedValue.where(SecurityContext.getScopedValue(), credential).run(() -> doChain(req, resp, chain));
    }

    private void doChain(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        try {
            chain.doFilter(req, resp);
        } catch (Throwable e) {
            HttpHelper.handleExceptionMapper(resp, e);
        }
    }
}
