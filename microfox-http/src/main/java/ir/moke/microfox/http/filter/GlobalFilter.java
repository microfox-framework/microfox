package ir.moke.microfox.http.filter;

import ir.moke.microfox.exception.MicrofoxException;
import ir.moke.microfox.utils.TtyAsciiCodecs;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class GlobalFilter implements Filter, TtyAsciiCodecs {
    private static final Logger logger = LoggerFactory.getLogger(GlobalFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            String uri = req.getRequestURI();
            LocalDateTime before = LocalDateTime.now();
            chain.doFilter(request, response);
            LocalDateTime after = LocalDateTime.now();
            long duration = ChronoUnit.MILLIS.between(before, after);
            logger.debug("Request {} processed in {} ms", uri, duration);
        } catch (IOException | ServletException e) {
            throw new MicrofoxException(e);
        }
    }
}
