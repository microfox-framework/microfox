package ir.moke.microfox.http.filter;

import ir.moke.microfox.utils.TtyAsciiCodecs;
import jakarta.servlet.*;
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
            LocalDateTime before = LocalDateTime.now();
            chain.doFilter(request, response);
            LocalDateTime after = LocalDateTime.now();
            long duration = ChronoUnit.MILLIS.between(before, after);
            logger.info("Request processed in {} ms{}", BACKGROUND_BLUE, duration);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
