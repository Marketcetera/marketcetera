package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Thrown when a single entity query ends up retrieving multiple results.
 * This might happen if a unique constraint hasn't been created on the
 * entity table and it allows multiple entities to be created with the
 * same value of an attribute that should otherwise be unique
 *
 * @see javax.persistence.NonUniqueResultException
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class NonUniqueResultException extends PersistenceException {
    private static final long serialVersionUID = 5796642055913880108L;

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */
    public NonUniqueResultException(Throwable cause) {
        super(cause);
    }
}
