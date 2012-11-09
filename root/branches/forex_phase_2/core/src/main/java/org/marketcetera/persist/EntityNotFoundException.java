package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * This exception is thrown when entity being accessed cannot be found.
 *
 * @see javax.persistence.EntityNotFoundException
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class EntityNotFoundException extends PersistenceException {
    private static final long serialVersionUID = -3310994307054230400L;

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }
}
