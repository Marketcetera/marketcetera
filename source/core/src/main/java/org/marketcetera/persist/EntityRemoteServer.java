package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.Messages.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javax.persistence.*;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Hashtable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
/* $License$ */

/**
 * Provides services to persist entities to a local database.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
// this class operates locally, none of its operations require serialization
@SuppressWarnings("serial")
class EntityRemoteServer extends EntityRemoteServices {
    /**
     * Saves the supplied entity
     *
     * @param entity The entity that needs to be saved.
     * @param context the persist context
     *
     * @return The save result instance
     *
     * @throws PersistenceException if there was an error saving the
     * entity
     *
     * @see EntityBase#saveLocal(javax.persistence.EntityManager, PersistContext)
     */
    public SaveResult save(final EntityBase entity,
                           final PersistContext context)
            throws org.marketcetera.persist.PersistenceException {
        return execute(new Transaction<SaveResult>() {
            public SaveResult execute(EntityManager em,
                                      PersistContext context)
                    throws org.marketcetera.persist.PersistenceException {
                return entity.saveLocal(em, context);
            }
        }, context);
    }

    /**
     * Removes the supplied entity from the database
     *
     * @param entity The entity that needs to be deleted.
     * @param context the persist context
     *
     * @throws PersistenceException there was an error deleting the entity
     *
     * @see EntityBase#deleteLocal(javax.persistence.EntityManager, PersistContext)
     */
    public SaveResult delete(final EntityBase entity,
                             final PersistContext context)
            throws org.marketcetera.persist.PersistenceException {
        return execute(new Transaction<SaveResult>() {
            public SaveResult execute(EntityManager em, PersistContext context)
                    throws org.marketcetera.persist.PersistenceException {
                return entity.deleteLocal(em, context);
            }
        }, context);
    }

    /**
     * Runs the supplied query and returns its results back.
     *
     * @param query The query instance that needs to be executed.
     *
     * @return The results of the query.
     *
     * @throws PersistenceException if there was an error executing
     * the query
     *
     * @see QueryBase#executeLocal(javax.persistence.EntityManager, java.util.List)    
     */
    public <T> List<QueryResults<T>> execute(
            final QueryBase query,
            final List<QueryProcessor<T>> processors)
            throws org.marketcetera.persist.PersistenceException {
        return execute(new Transaction<List<QueryResults<T>>>() {
            public List<QueryResults<T>> execute(EntityManager em,
                                                 PersistContext context)
                    throws org.marketcetera.persist.PersistenceException {
                return query.executeLocal(em,processors);
            }
        }, null);
    }

    /**
     * Executes the supplied transaction. Invocations to this method
     * can be nested. The transaction is created and committed or
     * rolled-back only when the outer-most invocation completes.
     * <p>
     * Note that the lifecycle of the EntityManager is currently the
     * same as that of the transaction. ie. a new EntityManager is
     * created for every transaction and the entity manager is closed
     * when the transaction is complete.
     *
     * @param t The transaction instance to be executed.
     * @param context the persist context, if any.
     *
     * @return The results from a transaction
     *
     * @throws PersistenceException if there was an error executing
     * the transaction.
     */
    public <R> R execute(Transaction<R> t, PersistContext context)
            throws org.marketcetera.persist.PersistenceException {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        boolean alreadyInTxn = et.isActive();
        if(alreadyInTxn) {
            //If we are already in a transaction, simply
            //execute and return
            return t.execute(em,context);
        } else {
            //We are not in a transaction, create a top-level
            //transaction and do commit, rollback, exception translation
            try {
                et.begin();
                SLF4JLoggerProxy.debug(this,"Beginning Transaction {}",et);
                try {
                    R value = t.execute(em, context);
                    et.commit();
                    SLF4JLoggerProxy.debug(this,"Transaction {} committed",et);
                    return value;
                } finally {
                    try {
                        if(et.isActive()) {
                            et.rollback();
                            SLF4JLoggerProxy.debug(this,"Transaction {} rolled back",et);
                        }
                    } finally {
                        entityManager.set(null);
                        em.close();
                    }
                }
            } catch (javax.persistence.PersistenceException e) {
                SLF4JLoggerProxy.debug(this,"Got Persistence Exception",e);
                //Translate exceptions
                translateAndThrow(e);
                //Needed to compile, the method above, always throws exception
                return null;
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.debug(this,"Got Runtime Exception",e);
                if(e.getCause() instanceof org.marketcetera.persist.PersistenceException) {
                    //Unwrap with nested known exception
                    throw (org.marketcetera.persist.PersistenceException)e.getCause();
                } else {
                    throw e;
                }
            }
        }
    }

    private void translateAndThrow(javax.persistence.PersistenceException e)
            throws org.marketcetera.persist.PersistenceException {
        //Translate the exception using the exception translation table.
        Constructor<? extends org.marketcetera.persist.PersistenceException> constr =
                exceptionTable.get(e.getClass());
        try {
            if(constr != null) {
                throw constr.newInstance(e);
            } else {
                //if matching subclass is not found, create a top-level
                //exception class.
                throw new PersistenceException(e);
            }
        } catch (InstantiationException e1) {
            throw new PersistSetupException(e1,EXCEPTION_TRANSLATE_ISSUE);
        } catch (IllegalAccessException e1) {
            throw new PersistSetupException(e1,EXCEPTION_TRANSLATE_ISSUE);
        } catch (InvocationTargetException e1) {
            throw new PersistSetupException(e1,EXCEPTION_TRANSLATE_ISSUE);
        }
    }

    /**
     * Creates an instance
     *
     * @param emf The entity manager factory instance that is used for
     * all persistence operations. Cannot be null.
     *
     * @throws PersistSetupException If there's an error creating
     * the instance
     */
    EntityRemoteServer(EntityManagerFactory emf) throws PersistSetupException {
        super();
        if(emf == null) {
            throw new NullPointerException();
        }
        entityManagerFactory = emf;
        try {
            exceptionTable.put(javax.persistence.EntityExistsException.class,
                    org.marketcetera.persist.EntityExistsException.
                            class.getDeclaredConstructor(
                            Throwable.class));
            exceptionTable.put(javax.persistence.EntityNotFoundException.class,
                    org.marketcetera.persist.EntityNotFoundException.
                            class.getDeclaredConstructor(
                            Throwable.class));
            exceptionTable.put(javax.persistence.NonUniqueResultException.class,
                    org.marketcetera.persist.NonUniqueResultException.
                            class.getDeclaredConstructor(
                            Throwable.class));
            exceptionTable.put(javax.persistence.NoResultException.class,
                    org.marketcetera.persist.NoResultException.
                            class.getDeclaredConstructor(
                            Throwable.class));
            exceptionTable.put(javax.persistence.OptimisticLockException.class,
                    org.marketcetera.persist.OptimisticLockException.
                            class.getDeclaredConstructor(
                            Throwable.class));
            exceptionTable.put(javax.persistence.RollbackException.class,
                    org.marketcetera.persist.RollbackException.
                            class.getDeclaredConstructor(
                            Throwable.class));
            exceptionTable.put(javax.persistence.TransactionRequiredException.class,
                    org.marketcetera.persist.TransactionRequiredException.
                            class.getDeclaredConstructor(
                            Throwable.class));
            SLF4JLoggerProxy.debug(this,"Exception Table: {}",exceptionTable);
        } catch (NoSuchMethodException e) {
            throw new PersistSetupException(e,EXCEPTION_TRANSLATE_ISSUE);
        }
    }

    /**
     * Returns the entity manager instance for the current thread.
     * If an entity manager instance doesn't exist, its created.
     *
     * @return The entity manager instance for the current thread.
     * 
     * @throws org.marketcetera.persist.PersistenceException if
     * there was an issue in entity manager creation.
     */
    private EntityManager getEntityManager() throws org.marketcetera.persist.PersistenceException {
        if(entityManager.get() == null) {
            SLF4JLoggerProxy.debug(this,"Creating a new Entity Manager");
            entityManager.set(entityManagerFactory.createEntityManager());
        }
        return entityManager.get();
    }

    /**
     * Keeps track of the entity manager instance for the current thread.
     * The entity manager instance for the current thread is not-null
     * if an attempt is made to fetch it from within a
     * {@link Transaction transaction}. Otherwise the entity manager
     * instance is null.
     */
    private static ThreadLocal<EntityManager> entityManager =
            new ThreadLocal<EntityManager>();
    private static Hashtable<Class<? extends javax.persistence.PersistenceException>,
            Constructor<? extends org.marketcetera.persist.PersistenceException>> exceptionTable =
            new Hashtable<Class<? extends PersistenceException>,
                    Constructor<? extends org.marketcetera.persist.PersistenceException>>();
    private final EntityManagerFactory entityManagerFactory;
}
