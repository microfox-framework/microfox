package ir.moke.microfox.http.filter;

import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.Route;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.api.http.security.Credential;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.http.HttpUtils;
import ir.moke.microfox.http.SecurityContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static ir.moke.microfox.http.HttpUtils.findMatchingRouteInfo;

public class SecurityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        Method method = Method.valueOf(req.getMethod().toUpperCase());

        findMatchingRouteInfo(req.getRequestURI(), method)
                .ifPresentOrElse(routeInfo -> applySecurity(routeInfo.route(), req, resp, chain),
                        () -> doChain(req, resp, chain));
    }

    private void applySecurity(Route route, HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        if (route.securityStrategy()  == null) {
            doChain(req, resp, chain); // No security required
            return;
        }

        SecurityStrategy strategy = route.securityStrategy();
        if (!strategy.isRequired()) {
            doChain(req, resp, chain);
            return;
        }

        Credential credential = strategy.authenticate(HttpUtils.getRequest(req));
        if (credential == null) {
            sendError(resp, StatusCode.UNAUTHORIZED, "Unauthorized");
            return;
        }

        if (!strategy.authorize(credential, route.roles(), route.scopes())) {
            sendError(resp, StatusCode.FORBIDDEN, "Forbidden");
            return;
        }

        // Store into SecurityContext for business layer
        SecurityContext.setCredential(credential);

        doChain(req, resp, chain);

        SecurityContext.clear();
    }

    private void doChain(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        try {
            chain.doFilter(req, resp);
        } catch (IOException | ServletException e) {
            throw new MicrofoxException(e);
        }
    }

    private void sendError(HttpServletResponse resp, StatusCode code, String message) {
        try {
            resp.setStatus(code.getCode());
            resp.getOutputStream().write(message.getBytes());
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }
}
