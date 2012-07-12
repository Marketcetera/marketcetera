package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Represents a database transaction. The lifecycles
 * of the entity manager and the transaction are the same, ie.
 * The EntityManager instance is created right before the transaction
 * is started and its closed as soon as the transaction is complete.
 *
 * @see org.marketcetera.persist.EntityBase#executeRemote(Transaction, PersistContext) 
 */
public interface Transaction<R> extends Serializable {
    /**
     * This method should be over-ridden to execute the code
     * that needs to run as an atomic database transaction.
     *
     * @param em      The entity manager instance for the transaction
     * @param context the persist context, if any.
     *
     * @return The transaction result, if any
     * 
     * @throws PersistenceException if there's an error running the
     * transaction.
     */
    public R execute(EntityManager em, PersistContext context)
            throws PersistenceException;
}
