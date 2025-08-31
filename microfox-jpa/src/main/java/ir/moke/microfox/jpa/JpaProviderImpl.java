package ir.moke.microfox.jpa;

import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import ir.moke.microfox.exception.MicrofoxException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.TransactionException;

import java.util.function.Consumer;

public class JpaProviderImpl implements JpaProvider {
    @Override
    public <T> T jpa(String identity, Class<T> repositoryClass) {
        return JpaFactory.create(repositoryClass, identity);
    }

    @Override
    public <T> void jpa(String identity, Class<T> repositoryClass, TransactionPolicy policy, Consumer<T> consumer) {
        EntityManager em = JpaFactory.getEntityManager(identity);
        EntityTransaction tx = em.getTransaction();

        try {
            switch (policy) {
                case REQUIRED_NEW -> requiredNewTx(identity, repositoryClass, consumer);
                case MANDATORY -> mandatoryTx(identity, repositoryClass, consumer, tx);
                case NEVER -> neverTx(identity, repositoryClass, consumer, tx);
                case REQUIRED -> requiredTx(identity, repositoryClass, consumer, tx);
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new MicrofoxException("Transaction failed", e);
        } finally {
            JpaFactory.closeEntityManager(identity);
        }
    }

    private <T> void neverTx(String identity, Class<T> repositoryClass, Consumer<T> consumer, EntityTransaction tx) {
        if (tx.isActive()) throw new IllegalStateException("Transaction exists but NEVER required");
        consumer.accept(jpa(identity, repositoryClass));
    }

    private <T> void mandatoryTx(String identity, Class<T> repositoryClass, Consumer<T> consumer, EntityTransaction tx) {
        if (!tx.isActive()) throw new IllegalStateException("Transaction is not currently active.");
        requiredTx(identity, repositoryClass, consumer, tx);
    }

    private <T> void requiredNewTx(String identity, Class<T> repositoryClass, Consumer<T> consumer) {
        EntityManager em = JpaFactory.createEntityManager(identity);
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            consumer.accept(jpa(identity, repositoryClass));
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new TransactionException("Transaction failed", e);
        } finally {
            JpaFactory.closeEntityManager(identity);
        }
    }

    private <T> void requiredTx(String identity, Class<T> repositoryClass, Consumer<T> consumer, EntityTransaction tx) {
        if (!tx.isActive()) tx.begin();
        try {
            consumer.accept(jpa(identity, repositoryClass));
            if (!tx.isActive()) tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
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
