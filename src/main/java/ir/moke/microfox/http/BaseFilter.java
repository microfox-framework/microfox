package ir.moke.microfox.http;

import jakarta.servlet.Filter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static ir.moke.microfox.http.HttpUtils.findMatchingFilterInfo;

@WebFilter("/*")
class BaseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        findMatchingFilterInfo(req.getRequestURI())
                .ifPresentOrElse(item -> applyFilter(item, req, resp, chain), () -> doChain(req, resp, chain));
    }

    private static void applyFilter(FilterInfo item, HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        item.filter().handle(new Request(req), new Response(resp));
        doChain(req, resp, chain);
    }

    private static void doChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
