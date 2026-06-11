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

import java.util.List;

import static ir.moke.microfox.http.HttpUtils.findMatchingFilterInfo;

public class BaseFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(BaseFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        List<FilterInfo> list = findMatchingFilterInfo(req.getRequestURI());
        applyFilter(0, list, req, resp, chain);
    }

    private static void applyFilter(int index, List<FilterInfo> filterList, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        if (index >= filterList.size()) {
            doChain(request, response, filterChain);
            return;
        }

        FilterInfo filterInfo = filterList.get(index);

        Request req = HttpUtils.getRequest(request);
        Response resp = HttpUtils.getResponse(response);
        Chain chain = (_, _) -> applyFilter(index + 1, filterList, request, response, filterChain);
        filterInfo.filter().handle(req, resp, chain);
    }

    private static void doChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Throwable e) {
            HttpUtils.handleExceptionMapper(response, e);
        }
    }
}
