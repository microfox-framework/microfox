package ir.moke.microfox.api.jpa;

import java.util.function.Consumer;
import java.util.function.Function;

public interface JpaProvider {
    <T> T jpa(String identity, Class<T> repositoryClass);

    <T> void jpaTx(String identity, Class<T> repositoryClass, Consumer<T> consumer);

    void jpaTxBegin(String persistenceUnitName);

    void jpaTxCommit(String persistenceUnitName);

    void jpaTxRollback(String persistenceUnitName);

    void jpaPrintCreateSchemaSQL(String persistenceUnitName);

    void jpaPrintUpdateSchemaSQL(String persistenceUnitName);
}
