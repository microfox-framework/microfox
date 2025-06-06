package ir.moke.microfox.api.jpa;

import java.util.function.Consumer;

public interface JpaProvider {
    <T> void jpa(Class<T> repositoryClass, String persistenceUnitName, Consumer<T> consumer);

    <T> void jpaTx(Class<T> repositoryClass, String persistenceUnitName, Consumer<T> consumer);

    void jpaTxBegin(String persistenceUnitName);

    void jpaTxCommit(String persistenceUnitName);

    void jpaTxRollback(String persistenceUnitName);

    void jpaPrintCreateSchemaSQL(String persistenceUnitName);

    void jpaPrintUpdateSchemaSQL(String persistenceUnitName);
}
