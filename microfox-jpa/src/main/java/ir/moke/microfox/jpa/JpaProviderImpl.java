package ir.moke.microfox.jpa;

import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.exception.MicrofoxException;

import java.util.function.Consumer;
import java.util.function.Function;

public class JpaProviderImpl implements JpaProvider {
    @Override
    public <T> T jpa(String identity, Class<T> repositoryClass) {
        return JpaFactory.create(repositoryClass, identity);
    }

    @Override
    public <T> void jpaTx(String identity, Class<T> repositoryClass, Consumer<T> consumer) {
        try {
            T t = JpaFactory.create(repositoryClass, identity);
            JpaFactory.beginTx(identity);
            consumer.accept(t);
            JpaFactory.commitTx(identity);
        } catch (Exception e) {
            JpaFactory.rollbackTx(identity);
            throw new MicrofoxException(e);
        } finally {
            JpaFactory.closeEntityManager(identity);
        }

    }

    @Override
    public void jpaTxBegin(String identity) {
        JpaFactory.beginTx(identity);
    }

    @Override
    public void jpaTxCommit(String identity) {
        JpaFactory.commitTx(identity);
    }

    @Override
    public void jpaTxRollback(String identity) {
        JpaFactory.rollbackTx(identity);
    }

    @Override
    public void jpaPrintCreateSchemaSQL(String identity) {
        JpaQueryGenerator.createSchema(identity);
    }

    @Override
    public void jpaPrintUpdateSchemaSQL(String identity) {
        JpaQueryGenerator.updateSchema(identity);
    }
}
