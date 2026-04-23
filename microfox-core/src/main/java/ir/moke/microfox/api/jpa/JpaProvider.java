package ir.moke.microfox.api.jpa;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface JpaProvider {
    void register(String identity, List<String> scanPackages, Map<String, Object> settings);

    void unregister(String identity);

    void jpa(String identity, TransactionPolicy policy, Integer txTimeout, Runnable runnable);

    <T> T jpa(String identity, Class<T> repositoryClass);

    <T> void jpa(String identity, Class<T> repositoryClass, TransactionPolicy policy, Integer txTimeout, Consumer<T> consumer);

    void jpaPrintCreateSchemaSQL(String identity);

    void jpaPrintUpdateSchemaSQL(String identity);

    void rollback(String identity);
}
