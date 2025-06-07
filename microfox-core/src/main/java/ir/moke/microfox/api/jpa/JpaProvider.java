package ir.moke.microfox.api.jpa;

import java.util.function.Consumer;
import java.util.function.Function;

public interface JpaProvider {
    <T, R> R jpa(Class<T> repositoryClass, String persistenceUnitName, Function<T, R> function);

    <T> void jpaTx(Class<T> repositoryClass, String persistenceUnitName, Consumer<T> consumer);

    void jpaTxBegin(String persistenceUnitName);

    void jpaTxCommit(String persistenceUnitName);

    void jpaTxRollback(String persistenceUnitName);

    void jpaPrintCreateSchemaSQL(String persistenceUnitName);

    void jpaPrintUpdateSchemaSQL(String persistenceUnitName);
}
