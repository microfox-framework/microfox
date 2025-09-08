package ir.moke.microfox.http.filter;

import ir.moke.microfox.http.FilterInfo;
import ir.moke.microfox.http.RequestImpl;
import ir.moke.microfox.http.ResponseImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
        boolean doChain = item.filter().handle(new RequestImpl(req), new ResponseImpl(resp));
        if (doChain) doChain(req, resp, chain);
    }

    private static void doChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
