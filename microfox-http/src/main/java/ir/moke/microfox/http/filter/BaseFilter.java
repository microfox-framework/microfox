package ir.moke.microfox.http.filter;

import ir.moke.microfox.api.http.Chain;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.http.FilterInfo;
import ir.moke.microfox.http.HttpUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static ir.moke.microfox.http.HttpUtils.findMatchingFilterInfo;

public class BaseFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(BaseFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        Consumer<FilterInfo> filterInfoConsumer = item -> applyFilter(item, req, resp, chain);
        Runnable chainRunner = () -> doChain(req, resp, chain);

        findMatchingFilterInfo(req.getRequestURI()).ifPresentOrElse(filterInfoConsumer, chainRunner);
    }

    private static void applyFilter(FilterInfo item, HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) {
        Request mic_req = HttpUtils.getRequest(req);
        Response mic_resp = HttpUtils.getResponse(resp);
        Chain mic_chain = (_, _) -> doChain(req, resp, filterChain);
        item.filter().handle(mic_req, mic_resp, mic_chain);
    }

    private static void doChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpUtils.handleExceptionMapper(response, e);
        }
    }
}
