package ir.moke.microfox.api.jpa;

import jakarta.persistence.EntityManager;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface JpaProvider {
    void registerWithPackages(String identity, Set<String> scanPackages, Map<String, Object> settings);

    void registerWithEntities(String identity, Set<Class<?>> entities, Map<String, Object> settings);

    void unregister(String identity);

    void jpaPrintCreateSchemaSQL(String identity);

    void jpaPrintUpdateSchemaSQL(String identity);

    void rollback(String identity);

    void jpa(String identity, TransactionPolicy policy, Consumer<EntityManager> consumer);

    void jpa(String identity, TransactionPolicy policy, Runnable consumer);

    void openEntityManager(String identity, Consumer<EntityManager> consumer);
}
