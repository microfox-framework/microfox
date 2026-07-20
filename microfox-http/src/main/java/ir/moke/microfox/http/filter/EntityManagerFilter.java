package ir.moke.microfox.http.filter;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.api.http.Chain;
import ir.moke.microfox.api.http.Filter;
import ir.moke.microfox.api.http.Request;
import ir.moke.microfox.api.http.Response;
import ir.moke.microfox.exception.MicroFoxException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityManagerFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerFilter.class);

    private final String identity;

    public EntityManagerFilter(String identity) {
        this.identity = identity;
    }

    @Override
    public void handle(Request request, Response response, Chain chain) {
        if (this.identity == null) {
            chain.doFilter(request, response);
            return;
        }

        MicroFox.openEntityManager(identity, em -> {
            try {
                chain.doFilter(request, response);

                if (request.isAsyncSupported() && request.isAsyncStarted()) {
                    asyncContextCleaner(request, em);
                    return;
                }

                close(em);
            } catch (Exception e) {
                close(em);
                throw new MicroFoxException(e);
            }
        });
    }

    private void asyncContextCleaner(Request request, EntityManager em) {
        AsyncContext asyncContext = request.asyncContext();
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) {
                close(em);
            }

            @Override
            public void onTimeout(AsyncEvent event) {
                close(em);
            }

            @Override
            public void onError(AsyncEvent event) {
                close(em);
            }

            @Override
            public void onStartAsync(AsyncEvent event) {
                event.getAsyncContext().addListener(this);
            }
        });
    }

    private static void close(EntityManager em) {
        if (em != null && em.isOpen()) em.close();
    }
}
