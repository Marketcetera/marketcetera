package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import static org.marketcetera.persist.Messages.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage0P;

import javax.persistence.*;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Hashtable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
/* $License$ */

/**
 * Provides services to persist entities to a local database.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
// this class operates locally, none of its operations require serialization
@SuppressWarnings("serial") //$NON-NLS-1$
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
                SLF4JLoggerProxy.debug(this,"Beginning Transaction {}",et); //$NON-NLS-1$
                try {
                    R value = t.execute(em, context);
                    et.commit();
                    SLF4JLoggerProxy.debug(this,"Transaction {} committed",et); //$NON-NLS-1$
                    return value;
                } finally {
                    try {
                        if(et.isActive()) {
                            et.rollback();
                            SLF4JLoggerProxy.debug(this,"Transaction {} rolled back",et); //$NON-NLS-1$
                        }
                    } finally {
                        entityManager.set(null);
                        em.close();
                    }
                }
            } catch (javax.persistence.PersistenceException e) {
                SLF4JLoggerProxy.debug(this,"Got Persistence Exception",e); //$NON-NLS-1$
                //Translate exceptions
                translateAndThrow(e);
                //Needed to compile, the method above, always throws exception
                return null;
            } catch (RuntimeException e) {
                SLF4JLoggerProxy.debug(this,"Got Runtime Exception",e); //$NON-NLS-1$
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
        ExceptionTranslator translator =
                exceptionTable.get(e.getClass());
        try {
            if(translator != null) {
                throw translator.translate(e);
            } else {
                //if matching subclass is not found, create a top-level
                //exception class.
                throw new org.marketcetera.persist.PersistenceException(e,
                        UNEXPECTED_ERROR);
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
                    new EntityExistsTranslator());
            exceptionTable.put(javax.persistence.EntityNotFoundException.class,
                    new DefaultTranslator(org.marketcetera.persist.EntityNotFoundException.class));
            exceptionTable.put(javax.persistence.NonUniqueResultException.class,
                    new DefaultTranslator(org.marketcetera.persist.NonUniqueResultException.class));
            exceptionTable.put(javax.persistence.NoResultException.class,
                    new DefaultTranslator(org.marketcetera.persist.NoResultException.class));
            exceptionTable.put(javax.persistence.OptimisticLockException.class,
                    new OptimisticLockTranslator());
            exceptionTable.put(javax.persistence.RollbackException.class,
                    new DefaultTranslator(org.marketcetera.persist.RollbackException.class));
            exceptionTable.put(javax.persistence.TransactionRequiredException.class,
                    new DefaultTranslator(org.marketcetera.persist.TransactionRequiredException.class));
            SLF4JLoggerProxy.debug(this,"Exception Table: {}",exceptionTable); //$NON-NLS-1$
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
            SLF4JLoggerProxy.debug(this,"Creating a new Entity Manager"); //$NON-NLS-1$
            entityManager.set(entityManagerFactory.createEntityManager());
        }
        return entityManager.get();
    }

    /**
     * Translates a JPA exception to a system exception.
     */
    private static interface ExceptionTranslator {
        /**
         * Translate the supplied JPA exception to a persistence exception.
         *
         * @param exception the JPA exception
         *
         * @return the translated exception
         *
         * @throws InvocationTargetException if there was an error
         * @throws InstantiationException if there was an error
         * @throws IllegalAccessException if there was an error
         * @throws org.marketcetera.persist.PersistenceException if there was
         * an error
         */
        org.marketcetera.persist.PersistenceException translate(
                PersistenceException exception)
                throws InvocationTargetException,
                InstantiationException,
                IllegalAccessException,
                org.marketcetera.persist.PersistenceException;
    }

    /**
     * Default exception translator. The translated exception is
     * created without a custom message and wraps the original
     * exception as a nested exception.
     */
    private static class DefaultTranslator implements ExceptionTranslator {
        private DefaultTranslator(Class<? extends
                org.marketcetera.persist.PersistenceException> clazz)
                throws NoSuchMethodException {
            mConstructor = clazz.getDeclaredConstructor(Throwable.class);
        }

        @Override
        public org.marketcetera.persist.PersistenceException translate(
                PersistenceException exception)
                throws InvocationTargetException,
                InstantiationException,
                IllegalAccessException {
            return mConstructor.newInstance(exception);
        }
        private Constructor<? extends
                org.marketcetera.persist.PersistenceException> mConstructor;
    }

    /**
     * A translator for <code>OptimisticLockException</code>.
     */
    private static class OptimisticLockTranslator implements ExceptionTranslator {
        @Override
        public org.marketcetera.persist.PersistenceException translate(
                PersistenceException exception) {
            javax.persistence.OptimisticLockException ole =
                    (javax.persistence.OptimisticLockException)exception;
            Object o = ole.getEntity();
            return new OptimisticLockException(exception,
                    new I18NBoundMessage1P(OPTMISTIC_LOCK_ERROR,
                            getEntityName(o)));
        }
    }

    private static class EntityExistsTranslator implements ExceptionTranslator {
        public org.marketcetera.persist.PersistenceException translate(
                PersistenceException exception)
                throws InvocationTargetException,
                InstantiationException,
                IllegalAccessException,
                org.marketcetera.persist.PersistenceException {
            return new EntityExistsException(exception,
                    VendorUtils.getEntityExistsMessage(
                            (javax.persistence.EntityExistsException)exception));
        }
    }

    /**
     * Returns the user-friendly entity name if a custom one has been
     * provided for the entity. If a custom name is not provided the
     * simple class name is used as the entity name. If the class of the
     * entity cannot be ascertained, a value indicating 'unknown' is returned.
     *
     * @param o the entity, its class name or its class object. Can be null,
     * if the value is null, default name is used.
     * 
     * @return the user-friendly name
     */
    static String getEntityName(Object o) {
        String entityName = null;
        Class clazz = null;
        if (o != null) {
            if(o instanceof EntityBase) {
                clazz = o.getClass();
            } else if (o instanceof Class) {
                clazz = (Class) o;
            } else if (o instanceof String) {
                try {
                    clazz = Class.forName((String)o);
                } catch (ClassNotFoundException ignored) {
                }
            }
            if(clazz != null && EntityBase.class.isAssignableFrom(clazz)) {
                try {
                    Method m = clazz.getDeclaredMethod("getUserFriendlyName");  //$NON-NLS-1$
                    if(Modifier.isStatic(m.getModifiers()) &&
                            I18NMessage0P.class.isAssignableFrom(m.getReturnType())) {
                        m.setAccessible(true);
                        I18NMessage0P msg = (I18NMessage0P) m.invoke(null);
                        if(msg != null) {
                            entityName = msg.getText();
                        }
                    }
                } catch (NoSuchMethodException ignore) {
                } catch (IllegalAccessException ignore) {
                } catch (InvocationTargetException ignore) {
                }
            }
        }
        if(entityName == null) {
            if(clazz != null) {
                entityName = DEFAULT_ENTITY_NAME.getText(clazz.getSimpleName());
            } else {
                entityName = UNKNOWN_ENTITY_NAME.getText();
            }
        }
        return entityName;
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
            ExceptionTranslator> exceptionTable =
            new Hashtable<Class<? extends PersistenceException>,
                    ExceptionTranslator>();
    private final EntityManagerFactory entityManagerFactory;
}
