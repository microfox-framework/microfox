package ir.moke.microfox.jpa;

import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Jpa {

    public static void persistence(String identity, TransactionPolicy policy, Runnable runnable) {
        persistence(identity, policy, _ -> runnable.run());
    }

    public static <T> void persistence(String identity, TransactionPolicy policy, Consumer<EntityManager> consumer) {
        Objects.requireNonNull(consumer, "Consumer must not be null");
        Objects.requireNonNull(policy, "Transaction policy must not be null");
        switch (policy) {
            case REQUIRED -> requiredTx(identity, consumer);
            case REQUIRED_NEW -> requiredNewTx(identity, consumer);
            case MANDATORY -> mandatoryTx(identity, consumer);
            case NEVER -> neverTx(identity, consumer);
            case NOT_SUPPORTED -> notSupportedTx(identity, consumer);
        }
    }

    /**
     * restrict persist for parent transaction so create new {@link EntityManager} without manage tx .
     *
     * @param identity database identity
     * @param consumer consumer
     * @param <T>      return type
     */
    private static <T> void notSupportedTx(String identity, Consumer<EntityManager> consumer) {
        ScopedValue<Map<String, EntityManager>> sv = JpaFactory.getScopedValue();
        try (EntityManager em = JpaFactory.getEntityManagerFactory(identity).createEntityManager()) {
            ScopedValue.where(sv, Map.of(identity, em)).run(() -> consumer.accept(em));
        }
    }

    private static <T> void neverTx(String identity, Consumer<EntityManager> consumer) {
        EntityManager em = JpaFactory.getEntityManager(identity);
        if (em != null && em.isOpen() && em.getTransaction().isActive()) {
            throw new IllegalStateException("Transaction exists but NEVER required");
        }
        consumer.accept(em);
    }

    private static <T> void mandatoryTx(String identity, Consumer<EntityManager> consumer) {
        EntityManager em = JpaFactory.getEntityManager(identity);
        if (em != null && em.isOpen() && em.getTransaction().isActive()) {
            consumer.accept(em);
        } else {
            throw new IllegalStateException("Transaction is not currently active.");
        }
    }

    private static <T> void requiredNewTx(String identity, Consumer<EntityManager> consumer) {
        EntityManagerFactory emf = JpaFactory.getEntityManagerFactory(identity);
        ScopedValue<Map<String, EntityManager>> sv = JpaFactory.getScopedValue();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.setTimeout(Integer.parseInt(MicroFoxEnvironment.getEnv("microfox.jpa.transaction-timeout")));

        ScopedValue.where(sv, Map.of(identity, em)).run(() -> {
            try {
                tx.begin();
                consumer.accept(em);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            } finally {
                if (em.isOpen()) em.close();
            }
        });
    }

    private static <T> void requiredTx(String identity, Consumer<EntityManager> consumer) {
        EntityManagerFactory emf = JpaFactory.getEntityManagerFactory(identity);
        ScopedValue<Map<String, EntityManager>> sv = JpaFactory.getScopedValue();

        EntityManager currentEm = sv.isBound() ? JpaFactory.getEntityManager(identity) : null;
        boolean isOwner = currentEm == null || !currentEm.isOpen() || !currentEm.getTransaction().isActive();

        EntityManager em = isOwner ? emf.createEntityManager() : currentEm;
        EntityTransaction tx = em.getTransaction();

        if (isOwner) {
            tx.setTimeout(Integer.parseInt(MicroFoxEnvironment.getEnv("microfox.jpa.transaction-timeout")));
        }

        ScopedValue.where(sv, Map.of(identity, em)).run(() -> {
            try {
                if (isOwner && !tx.isActive()) tx.begin();
                consumer.accept(em);
                if (isOwner && tx.isActive()) tx.commit();
            } catch (Exception e) {
                if (isOwner && tx.isActive()) tx.rollback();
                throw e;
            } finally {
                if (isOwner && em.isOpen()) em.close();
            }
        });
    }
}
