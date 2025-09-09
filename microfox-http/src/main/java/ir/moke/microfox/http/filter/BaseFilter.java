package ir.moke.microfox.http.filter;

import ir.moke.microfox.http.FilterInfo;
import ir.moke.microfox.http.HttpUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static ir.moke.microfox.http.HttpUtils.findMatchingFilterInfo;

public class BaseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        findMatchingFilterInfo(req.getRequestURI())
                .ifPresentOrElse(item -> applyFilter(item, req, resp, chain), () -> doChain(req, resp, chain));
    }

    private static void applyFilter(FilterInfo item, HttpServletRequest req, HttpServletResponse resp, FilterChain chain) {
        boolean doChain = item.filter().handle(HttpUtils.getRequest(req), HttpUtils.getResponse(resp));
        if (doChain) doChain(req, resp, chain);
    }

    private static void doChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpUtils.handleExceptionMapper(response, e);
        }
    }
}
