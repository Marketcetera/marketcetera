package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Thrown when the transaction commit fails 
 *
 * @see javax.persistence.RollbackException
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class RollbackException extends PersistenceException {
    private static final long serialVersionUID = -2550706630770810289L;

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */
    public RollbackException(Throwable cause) {
        super(cause);
    }
}
