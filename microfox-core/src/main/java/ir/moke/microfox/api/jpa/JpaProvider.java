package ir.moke.microfox.api.jpa;

import java.util.function.Consumer;

public interface JpaProvider {
    <T> T jpa(String identity, Class<T> repositoryClass);

    <T> void jpa(String identity, Class<T> repositoryClass, TransactionPolicy policy, Consumer<T> consumer);

    void jpaPrintCreateSchemaSQL(String persistenceUnitName);

    void jpaPrintUpdateSchemaSQL(String persistenceUnitName);

    void rollback(String persistenceUnitName);
}
