package ir.moke.microfox.jpa;

import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;
import java.util.function.Consumer;

public class JpaProviderImpl implements JpaProvider {

    @Override
    public void jpa(String identity, Integer txTimeout, Runnable runnable) {
        requiredTx(identity, null, _ -> runnable.run(), txTimeout);
    }

    @Override
    public void jpa(String identity, Runnable runnable) {
        jpa(identity, null, runnable);
    }

    @Override
    public <T> T jpa(String identity, Class<T> repositoryClass) {
        return JpaFactory.create(repositoryClass, identity);
    }

    @Override
    public <T> void jpa(String identity, Class<T> repositoryClass, TransactionPolicy policy, Consumer<T> consumer) {
        jpa(identity, repositoryClass, policy, null, consumer);
    }

    @Override
    public <T> void jpa(String identity, Class<T> repositoryClass, TransactionPolicy policy, Integer txTimeout, Consumer<T> consumer) {
        Objects.requireNonNull(consumer, "Consumer must not be null");
        Objects.requireNonNull(policy, "Transaction policy must not be null");
        switch (policy) {
            case REQUIRED -> requiredTx(identity, repositoryClass, consumer, txTimeout);
            case REQUIRED_NEW -> requiredNewTx(identity, repositoryClass, consumer, txTimeout);
            case MANDATORY -> mandatoryTx(identity, repositoryClass, consumer);
            case NEVER -> neverTx(identity, repositoryClass, consumer);
            case NOT_SUPPORTED -> notSupportedTx(identity, repositoryClass, consumer);
        }
    }

    /**
     * restrict persist for parent transaction so create new {@link EntityManager} without manage tx .
     *
     * @param identity        database identity
     * @param repositoryClass repository class
     * @param consumer        consumer
     * @param <T>             return type
     */
    private <T> void notSupportedTx(String identity, Class<T> repositoryClass, Consumer<T> consumer) {
        consumer.accept(jpa(identity, repositoryClass));
    }

    private <T> void neverTx(String identity, Class<T> repositoryClass, Consumer<T> consumer) {
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue(identity);
        EntityManager em = sv.get();
        if (em != null && em.isOpen() && em.getTransaction().isActive()) {
            throw new IllegalStateException("Transaction exists but NEVER required");
        }
        consumer.accept(jpa(identity, repositoryClass));
    }

    private <T> void mandatoryTx(String identity, Class<T> repositoryClass, Consumer<T> consumer) {
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue(identity);
        EntityManager em = sv.get();
        if (em != null && em.isOpen() && em.getTransaction().isActive()) {
            consumer.accept(jpa(identity, repositoryClass));
        } else {
            throw new IllegalStateException("Transaction is not currently active.");
        }
    }

    private <T> void requiredNewTx(String identity, Class<T> repositoryClass, Consumer<T> consumer, Integer txTimeout) {
        EntityManagerFactory emf = JpaFactory.getEntityManagerFactory(identity);
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue(identity);

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        JpaFactory.putScopedValue(identity, sv);
        ScopedValue.where(sv, em).run(() -> {
            try {
                if (txTimeout != null && txTimeout > 0) tx.setTimeout(txTimeout);
                tx.begin();
                if (repositoryClass != null) {
                    consumer.accept(jpa(identity, repositoryClass));
                } else {
                    consumer.accept(null);
                }
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            } finally {
                if (sv.get().isOpen()) sv.get().close();
                JpaFactory.removeScopedValue(identity);
            }
        });
    }

    private <T> void requiredTx(String identity, Class<T> repositoryClass, Consumer<T> consumer, Integer txTimeout) {
        EntityManagerFactory emf = JpaFactory.getEntityManagerFactory(identity);
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue(identity);
        boolean isOwner = !sv.isBound();
        EntityManager em = isOwner ? emf.createEntityManager() : sv.get();

        EntityTransaction tx = em.getTransaction();
        boolean isActive = tx.isActive();

        JpaFactory.putScopedValue(identity, sv);
        ScopedValue.where(sv, em).run(() -> {
            try {
                if (txTimeout != null && txTimeout > 0 && !isActive) tx.setTimeout(txTimeout);
                if (isOwner && !isActive) tx.begin();

                if (repositoryClass != null) {
                    consumer.accept(jpa(identity, repositoryClass));
                } else {
                    consumer.accept(null);
                }
                if (isOwner && tx.isActive()) tx.commit();
            } catch (Exception e) {
                if (isOwner && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                if (isOwner) {
                    if (sv.get() != null && sv.get().isOpen()) sv.get().close();
                    JpaFactory.removeScopedValue(identity);
                }
            }
        });
    }

    @Override
    public void jpaPrintCreateSchemaSQL(String identity) {
        JpaQueryGenerator.createSchema(identity);
    }

    @Override
    public void jpaPrintUpdateSchemaSQL(String identity) {
        JpaQueryGenerator.updateSchema(identity);
    }

    @Override
    public void rollback(String identity) {
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue(identity);
        EntityManager em = sv.get();
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
