package ir.moke.microfox.jpa;

import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.exception.MicrofoxException;

import java.util.function.Consumer;

public class JpaProviderImpl implements JpaProvider {
    @Override
    public <T> void jpa(Class<T> repositoryClass, String persistenceUnitName, Consumer<T> consumer) {
        T t = JpaFactory.create(repositoryClass, persistenceUnitName);
        consumer.accept(t);
    }

    @Override
    public <T> void jpaTx(Class<T> repositoryClass, String persistenceUnitName, Consumer<T> consumer) {
        try {
            T t = JpaFactory.create(repositoryClass, persistenceUnitName);
            JpaFactory.beginTx(persistenceUnitName);
            consumer.accept(t);
            JpaFactory.commitTx(persistenceUnitName);
        } catch (Exception e) {
            JpaFactory.rollbackTx(persistenceUnitName);
            throw new MicrofoxException(e);
        } finally {
            JpaFactory.closeEntityManager(persistenceUnitName);
        }

    }

    @Override
    public void jpaTxBegin(String persistenceUnitName) {
        JpaFactory.beginTx(persistenceUnitName);
    }

    @Override
    public void jpaTxCommit(String persistenceUnitName) {
        JpaFactory.commitTx(persistenceUnitName);
    }

    @Override
    public void jpaTxRollback(String persistenceUnitName) {
        JpaFactory.rollbackTx(persistenceUnitName);
    }

    @Override
    public void jpaPrintCreateSchemaSQL(String persistenceUnitName) {
        JpaQueryGenerator.createSchema(persistenceUnitName);
    }

    @Override
    public void jpaPrintUpdateSchemaSQL(String persistenceUnitName) {
        JpaQueryGenerator.updateSchema(persistenceUnitName);
    }
}
