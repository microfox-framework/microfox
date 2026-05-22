package ir.moke.microfox.jpa;

import ir.moke.microfox.api.jpa.JpaProvider;
import ir.moke.microfox.api.jpa.TransactionPolicy;
import jakarta.persistence.EntityManager;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class JpaProviderImpl implements JpaProvider {

    @Override
    public void registerWithEntities(String identity, Set<Class<?>> entities, Map<String, Object> settings) {
        JpaFactory.registerWithEntities(identity, entities, settings);
    }

    @Override
    public void registerWithPackages(String identity, Set<String> scanPackages, Map<String, Object> settings) {
        JpaFactory.registerWithPackage(identity, scanPackages, settings);
    }

    @Override
    public void unregister(String identity) {
        JpaFactory.unregister(identity);
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
        EntityManager em = JpaFactory.getEntityManager(identity);
        if (em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    @Override
    public void jpa(String identity, TransactionPolicy policy, Consumer<EntityManager> consumer) {
        Jpa.persistence(identity, policy, consumer);
    }

    @Override
    public void jpa(String identity, TransactionPolicy policy, Runnable runnable) {
        Jpa.persistence(identity, policy, runnable);
    }
}
