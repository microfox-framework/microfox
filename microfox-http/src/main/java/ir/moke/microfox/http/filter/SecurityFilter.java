package ir.moke.microfox.http.filter;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.ErrorObject;
import ir.moke.microfox.api.http.Method;
import ir.moke.microfox.api.http.RouteInfo;
import ir.moke.microfox.api.http.StatusCode;
import ir.moke.microfox.api.http.security.Credential;
import ir.moke.microfox.api.http.security.SecurityStrategy;
import ir.moke.microfox.exception.ExceptionMapper;
import ir.moke.microfox.exception.MicroFoxException;
import ir.moke.microfox.http.HttpUtils;
import ir.moke.microfox.http.SecurityContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static ir.moke.microfox.http.HttpUtils.findMatchingRouteInfo;

public class SecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ExceptionMapper<MicroFoxException> mapper = new ExceptionMapper<>() {
            @Override
            public ErrorObject toResponse(MicroFoxException throwable) {
                return new ErrorObject.Builder().setStatusCode(throwable.getStatusCode()).build();
            }
        };
        MicroFox.registerExceptionMapper(mapper);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        Method method = Method.valueOf(req.getMethod().toUpperCase());

        findMatchingRouteInfo(req.getRequestURI(), method)
                .ifPresentOrElse(routeInfo -> applySecurity(routeInfo, req, resp, chain),
                        () -> doChain(req, resp, chain));
    }

    private void applySecurity(RouteInfo routeInfo, HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        try {
            if (routeInfo.strategy() == null) {
                doChain(req, resp, chain); // No security required
                return;
            }

            SecurityStrategy strategy = routeInfo.strategy();
            if (!strategy.isRequired()) {
                doChain(req, resp, chain);
                return;
            }

            Credential credential = strategy.authenticate(HttpUtils.getRequest(req));
            if (credential == null) {
                throw new MicroFoxException(StatusCode.UNAUTHORIZED);
            }

            if (!strategy.authorize(credential, routeInfo.roles(), routeInfo.scopes())) {
                throw new MicroFoxException(StatusCode.FORBIDDEN);
            }

            // Store into SecurityContext for business layer
            ScopedValue.where(SecurityContext.getScopedValue(), credential).run(() -> doChain(req, resp, chain));
        } catch (Exception e) {
            if (e instanceof MicroFoxException mfe) throw mfe;
            throw new MicroFoxException(e);
        }
    }

    private void doChain(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        try {
            chain.doFilter(req, resp);
        } catch (IOException | ServletException e) {
            throw new MicroFoxException(StatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}
