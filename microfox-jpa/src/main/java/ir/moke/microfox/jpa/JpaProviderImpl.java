package ir.moke.microfox.jpa;

import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class JpaProviderImpl implements JpaProvider {
    @Override
    public <T> T jpa(String identity, Class<T> repositoryClass) {
        EntityManagerFactory emf = JpaFactory.getEntityManagerFactory(identity);
        return JpaFactory.create(repositoryClass, emf.createEntityManager());
    }

    private <T> T jpa(EntityManager em, Class<T> repositoryClass) {
        return JpaFactory.create(repositoryClass, em);
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
            case REQUIRED -> requiredTx(identity, repositoryClass, consumer, txTimeout, false);
            case REQUIRED_NEW -> requiredNewTx(identity, repositoryClass, consumer, txTimeout);
            case MANDATORY -> mandatoryTx(repositoryClass, consumer);
            case NEVER -> neverTx(repositoryClass, consumer);
            case NOT_SUPPORTED -> notSupportedTx(identity, repositoryClass, consumer);
        }
    }

    /**
     * restrict persist for parent transaction so create new {@link EntityManager} without manage tx .
     * @param identity database identity
     * @param repositoryClass repository class
     * @param consumer consumer
     * @param <T> return type
     */
    private <T> void notSupportedTx(String identity, Class<T> repositoryClass, Consumer<T> consumer) {
        consumer.accept(jpa(identity, repositoryClass));
    }

    private <T> void neverTx(Class<T> repositoryClass, Consumer<T> consumer) {
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue();
        EntityManager em = sv.get();
        if (em == null || em.isOpen() && em.getTransaction().isActive()) {
            throw new IllegalStateException("Transaction exists but NEVER required");
        }
        consumer.accept(jpa(em, repositoryClass));
    }

    private <T> void mandatoryTx(Class<T> repositoryClass, Consumer<T> consumer) {
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue();
        EntityManager em = sv.get();
        if (em != null && em.isOpen() && em.getTransaction().isActive()) {
            consumer.accept(jpa(em, repositoryClass));
        } else {
            throw new IllegalStateException("Transaction is not currently active.");
        }
    }

    private <T> void requiredNewTx(String identity, Class<T> repositoryClass, Consumer<T> consumer, Integer txTimeout) {
        requiredTx(identity, repositoryClass, consumer, txTimeout, true);
    }

    private <T> void requiredTx(String identity, Class<T> repositoryClass, Consumer<T> consumer, Integer txTimeout, boolean createNew) {
        EntityManagerFactory emf = JpaFactory.getEntityManagerFactory(identity);
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue();
        EntityManager em = createNew ? emf.createEntityManager() : sv.orElse(emf.createEntityManager());
        if (!em.isOpen() && !createNew) em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        if (txTimeout != null && txTimeout > 0 && !tx.isActive()) tx.setTimeout(txTimeout);
        AtomicBoolean txOwner = new AtomicBoolean(false);
        ScopedValue.where(sv, em).run(() -> {
            try {
                if (!tx.isActive()) {
                    tx.begin();
                    txOwner.set(true);
                }
                consumer.accept(jpa(sv.get(), repositoryClass));
                if (txOwner.get() && tx.isActive()) tx.commit();
            } catch (Throwable t) {
                if (txOwner.get() && tx.isActive()) tx.rollback();
                throw t;
            } finally {
                if (txOwner.get() && sv.get().isOpen()) sv.get().close();
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
    public void rollback() {
        ScopedValue<EntityManager> sv = JpaFactory.getEntityManagerScopedValue();
        EntityManager em = sv.get();
        if (em != null && em.getTransaction().isActive()) {
            EntityTransaction tx = em.getTransaction();
            tx.rollback();
        }
    }
}
