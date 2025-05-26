package ir.moke.microfox.http;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class EmbeddedFilterContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) {
        for (Class<?> filterClass : classes) {
            if (Filter.class.isAssignableFrom(filterClass)) {
                registerFilter(ctx, filterClass);
            }
        }
    }

    private void registerFilter(ServletContext ctx, Class<?> filterClass) {
        WebFilter webFilterAnnotation = filterClass.getDeclaredAnnotation(WebFilter.class);
        String name = extractServletName(filterClass, webFilterAnnotation);
        String[] urlPatterns = extractUrlPattern(webFilterAnnotation);
        Filter filter = createFilterInstance(filterClass);
        FilterRegistration.Dynamic filterReg = ctx.addFilter(name, filter);
        filterReg.addMappingForUrlPatterns(null, false, urlPatterns);
    }

    private static String[] extractUrlPattern(WebFilter webFilterAnnotation) {
        Set<String> urlPatterns = new HashSet<>();
        urlPatterns.addAll(Arrays.asList(webFilterAnnotation.value()));
        urlPatterns.addAll(Arrays.asList(webFilterAnnotation.urlPatterns()));
        return urlPatterns.toArray(new String[0]);
    }

    private static String extractServletName(Class<?> filterClass, WebFilter webFilterAnnotation) {
        return !webFilterAnnotation.filterName().isEmpty() ? webFilterAnnotation.filterName() : filterClass.getSimpleName();
    }

    private static Filter createFilterInstance(Class<?> filterClass) {
        try {
            return (Filter) filterClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
