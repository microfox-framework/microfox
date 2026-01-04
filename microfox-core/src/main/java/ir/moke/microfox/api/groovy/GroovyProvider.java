package ir.moke.microfox.api.groovy;

import java.io.File;
import java.util.function.Consumer;

public interface GroovyProvider {
    void eval(File file, Consumer<Object> result);

    void eval(String script, Consumer<Object> result);

    void parse(File file, Consumer<Class<?>> classConsumer);

    void parse(String script, Consumer<Class<?>> classConsumer);

    void parse(String script, ClassLoader parentClassLoader, Consumer<Class<?>> classConsumer);
    void parse(File file, ClassLoader parentClassLoader, Consumer<Class<?>> classConsumer);
}
