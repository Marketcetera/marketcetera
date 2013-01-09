package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Thrown when a transaction is required but is not active.
 * This exception should not be thrown in the system unless
 * JPA is used outside of the persistence infrastructure.
 * The persistence infrastructure ensures that every database
 * operation runs within the context of a transaction. 
 *
 * @see javax.persistence.TransactionRequiredException
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class TransactionRequiredException extends PersistenceException {
    private static final long serialVersionUID = 6215041377436947873L;

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */
    public TransactionRequiredException(Throwable cause) {
        super(cause);
    }
}
