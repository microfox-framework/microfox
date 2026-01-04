package ir.moke.microfox.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import ir.moke.microfox.api.groovy.GroovyProvider;
import ir.moke.microfox.exception.MicrofoxException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class GroovyProviderImpl implements GroovyProvider {
    private static final GroovyShell shell = new GroovyShell();

    @Override
    public void eval(File file, Consumer<Object> consumer) {
        try {
            Objects.requireNonNull(file, "file should not be null");
            Object evaluate = shell.evaluate(file);
            if (consumer != null) consumer.accept(evaluate);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void eval(String script, Consumer<Object> consumer) {
        try {
            Objects.requireNonNull(script, "script should not be null");
            Object evaluate = shell.evaluate(script);
            if (consumer != null) consumer.accept(evaluate);
        } catch (Exception e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void parse(File file, Consumer<Class<?>> classConsumer) {
        try (GroovyClassLoader gcl = new GroovyClassLoader()) {
            Objects.requireNonNull(file, "file should not be null");
            Class<?> aClass = gcl.parseClass(file);
            if (classConsumer != null) classConsumer.accept(aClass);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void parse(String script, Consumer<Class<?>> classConsumer) {
        try (GroovyClassLoader gcl = new GroovyClassLoader()) {
            Objects.requireNonNull(script, "script should not be null");
            Class<?> aClass = gcl.parseClass(script);
            if (classConsumer != null) classConsumer.accept(aClass);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void parse(String script, ClassLoader parentClassLoader, Consumer<Class<?>> classConsumer) {
        try (GroovyClassLoader gcl = new GroovyClassLoader(parentClassLoader)) {
            Objects.requireNonNull(script, "script should not be null");
            Class<?> aClass = gcl.parseClass(script);
            if (classConsumer != null) classConsumer.accept(aClass);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }

    @Override
    public void parse(File file, ClassLoader parentClassLoader, Consumer<Class<?>> classConsumer) {
        try (GroovyClassLoader gcl = new GroovyClassLoader()) {
            Objects.requireNonNull(file, "file should not be null");
            Class<?> aClass = gcl.parseClass(file);
            if (classConsumer != null) classConsumer.accept(aClass);
        } catch (IOException e) {
            throw new MicrofoxException(e);
        }
    }
}
