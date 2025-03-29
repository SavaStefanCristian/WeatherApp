package org.proiect.persistence.connection;



import lombok.Getter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;


public class DatabaseConnectionAbstract {
    @Getter
    private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;

    public DatabaseConnectionAbstract(String persistenceUnit) {
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
        entityManager = entityManagerFactory.createEntityManager();
    }

    public void close() throws IOException {
        entityManager.close();
        entityManagerFactory.close();
    }

    public void executeTransaction(Consumer<EntityManager> action) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            action.accept(entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            System.err.println("Transaction error: " + e.getLocalizedMessage());
            entityTransaction.rollback();
        }
    }

    public <T,R> R executeQueryTransaction(Function<EntityManager, T> action, Class<R> result) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        Object queryResult = null;

        try {
            entityTransaction.begin();
            queryResult = action.apply(entityManager);
            entityTransaction.commit();
        } catch (RuntimeException e) {
            System.err.println("Transaction error: " + e.getLocalizedMessage());
            entityTransaction.rollback();
        }

        return (R) queryResult;
    }

}