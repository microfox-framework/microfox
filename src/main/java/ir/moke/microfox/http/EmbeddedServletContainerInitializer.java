package ir.moke.microfox.http;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.HandlesTypes;
import jakarta.servlet.annotation.WebServlet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@HandlesTypes(Servlet.class)
public class EmbeddedServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) {
        if (classes != null) {
            for (Class<?> servletClass : classes) {
                if (Servlet.class.isAssignableFrom(servletClass)) {
                    registerServlet(ctx, servletClass);
                }
            }
        }
    }

    private static void registerServlet(ServletContext ctx, Class<?> servletClass) {
        WebServlet webServletAnnotation = servletClass.getDeclaredAnnotation(WebServlet.class);
        if (webServletAnnotation != null) {
            String name = extractServletName(servletClass, webServletAnnotation);
            String[] urlPatterns = extractUrlPattern(webServletAnnotation);
            Servlet servlet = createServletInstance(servletClass);
            ctx.addServlet(name, servlet).addMapping(urlPatterns);
        }
    }

    private static String[] extractUrlPattern(WebServlet webServletAnnotation) {
        Set<String> urlPatterns = new HashSet<>();
        urlPatterns.addAll(Arrays.asList(webServletAnnotation.value()));
        urlPatterns.addAll(Arrays.asList(webServletAnnotation.urlPatterns()));
        return urlPatterns.toArray(new String[0]);
    }

    private static String extractServletName(Class<?> servletClass, WebServlet webServletAnnotation) {
        return !webServletAnnotation.name().isEmpty() ? webServletAnnotation.name() : servletClass.getSimpleName();
    }

    private static Servlet createServletInstance(Class<?> servletClass) {
        try {
            return (Servlet) servletClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
