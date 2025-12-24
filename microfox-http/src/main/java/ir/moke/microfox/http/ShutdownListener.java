package ir.moke.microfox.http;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ShutdownListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ResourceHolder.closeAllSse();
        ResourceHolder.SSE_EXECUTOR.shutdown();
    }
}
