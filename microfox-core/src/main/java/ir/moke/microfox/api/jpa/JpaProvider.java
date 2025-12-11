package ir.moke.microfox.api.jpa;

import java.util.function.Consumer;

public interface JpaProvider {
    <T> T jpa(String identity, Class<T> repositoryClass);

    <T> void jpa(String identity, Class<T> repositoryClass, TransactionPolicy policy, Consumer<T> consumer);

    <T> void jpa(String identity, Class<T> repositoryClass, TransactionPolicy policy, Integer txTimeout, Consumer<T> consumer);

    void jpaPrintCreateSchemaSQL(String identity);

    void jpaPrintUpdateSchemaSQL(String identity);

    void rollback();
}
